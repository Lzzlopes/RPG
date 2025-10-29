package com.olddragon.charactercreator.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.olddragon.charactercreator.model.Character
import com.olddragon.charactercreator.model.combat.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CombatService : Service() {
    
    private val binder = CombatBinder()
    private val combatManager = CombatManager()
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    
    private val _activeCombat = MutableStateFlow<Combat?>(null)
    val activeCombat: StateFlow<Combat?> = _activeCombat
    
    private val _combatLog = MutableStateFlow<List<String>>(emptyList())
    val combatLog: StateFlow<List<String>> = _combatLog
    
    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing
    
    inner class CombatBinder : Binder() {
        fun getService(): CombatService = this@CombatService
    }
    
    override fun onBind(intent: Intent?): IBinder {
        return binder
    }
    
    /**
     * Inicia um novo combate
     */
    fun startNewCombat(
        playerCharacter: Character,
        difficulty: CombatDifficulty = CombatDifficulty.MEDIUM,
        onComplete: ((Combat) -> Unit)? = null
    ) {
        serviceScope.launch {
            _isProcessing.value = true
            _combatLog.value = emptyList()
            
            try {
                val playerCombatant = combatManager.createCombatantFromCharacter(playerCharacter)
                val enemy = EnemyGenerator.generateEnemyByDifficulty(difficulty)
                
                val combat = combatManager.startCombat(
                    playerCombatant = playerCombatant,
                    enemies = listOf(enemy)
                )
                
                _activeCombat.value = combat
                
                Log.d("CombatService", "Combate iniciado: ${playerCharacter.name} vs ${enemy.name}")
                
                onComplete?.invoke(combat)
                
            } catch (e: Exception) {
                Log.e("CombatService", "Erro ao iniciar combate", e)
            } finally {
                _isProcessing.value = false
            }
        }
    }
    
    /**
     * Executa o combate automaticamente
     */
    fun runAutoCombat(
        combat: Combat,
        delayBetweenRounds: Long = 2000L,
        onRoundComplete: ((CombatRound, List<String>) -> Unit)? = null,
        onCombatComplete: ((Combat) -> Unit)? = null
    ) {
        serviceScope.launch {
            _isProcessing.value = true
            val logs = mutableListOf<String>()
            
            try {
                val surpriseMessages = combatManager.processSurpriseCheck(combat)
                logs.addAll(surpriseMessages)
                _combatLog.value = logs.toList()
                delay(delayBetweenRounds)
                
                val initiativeMessages = combatManager.processInitiativeRoll(combat)
                logs.addAll(initiativeMessages)
                _combatLog.value = logs.toList()
                delay(delayBetweenRounds)
                
                var roundCount = 0
                val maxRounds = 20
                
                while (!combat.isFinished() && roundCount < maxRounds) {
                    val (round, roundMessages) = combatManager.processRound(combat)
                    logs.addAll(roundMessages)
                    _combatLog.value = logs.toList()
                    _activeCombat.value = combat
                    
                    onRoundComplete?.invoke(round, roundMessages)
                    
                    if (!combat.isFinished()) {
                        delay(delayBetweenRounds)
                    }
                    
                    roundCount++
                }
                
                if (roundCount >= maxRounds) {
                    logs.add("\n⚠️ Combate excedeu o limite de rodadas!")
                    _combatLog.value = logs.toList()
                }
                
                Log.d("CombatService", "Combate finalizado. Vencedor: ${combat.winnerId}")
                
                onCombatComplete?.invoke(combat)
                
            } catch (e: Exception) {
                Log.e("CombatService", "Erro durante combate", e)
                logs.add("\n❌ Erro durante o combate: ${e.message}")
                _combatLog.value = logs.toList()
            } finally {
                _isProcessing.value = false
            }
        }
    }
    
    /**
     * Executa apenas a próxima rodada
     */
    fun processNextRound(
        combat: Combat,
        onComplete: ((CombatRound, List<String>) -> Unit)? = null
    ) {
        serviceScope.launch {
            _isProcessing.value = true
            
            try {
                when (combat.state) {
                    CombatState.SURPRISE_CHECK -> {
                        val messages = combatManager.processSurpriseCheck(combat)
                        val currentLogs = _combatLog.value.toMutableList()
                        currentLogs.addAll(messages)
                        _combatLog.value = currentLogs
                    }
                    CombatState.INITIATIVE_ROLL -> {
                        val messages = combatManager.processInitiativeRoll(combat)
                        val currentLogs = _combatLog.value.toMutableList()
                        currentLogs.addAll(messages)
                        _combatLog.value = currentLogs
                    }
                    CombatState.IN_PROGRESS -> {
                        val (round, messages) = combatManager.processRound(combat)
                        val currentLogs = _combatLog.value.toMutableList()
                        currentLogs.addAll(messages)
                        _combatLog.value = currentLogs
                        _activeCombat.value = combat
                        
                        onComplete?.invoke(round, messages)
                    }
                    else -> {
                        Log.w("CombatService", "Combate em estado inválido: ${combat.state}")
                    }
                }
                
            } catch (e: Exception) {
                Log.e("CombatService", "Erro ao processar rodada", e)
            } finally {
                _isProcessing.value = false
            }
        }
    }
    
    /**
     * Limpa o combate atual
     */
    fun clearCombat() {
        _activeCombat.value = null
        _combatLog.value = emptyList()
        _isProcessing.value = false
    }
    
    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        Log.d("CombatService", "Serviço de combate destruído")
    }
}
