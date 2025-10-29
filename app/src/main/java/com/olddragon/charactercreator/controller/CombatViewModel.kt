package com.olddragon.charactercreator.controller

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.olddragon.charactercreator.data.CharacterDatabase
import com.olddragon.charactercreator.model.Character
import com.olddragon.charactercreator.model.combat.*
import com.olddragon.charactercreator.service.CombatService
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CombatViewModel(application: Application) : AndroidViewModel(application) {
    
    private val database = Room.databaseBuilder(
        application,
        CharacterDatabase::class.java,
        "character-database"
    ).fallbackToDestructiveMigration().build()
    
    private val combatDao = database.combatDao()
    
    private var combatService: CombatService? = null
    private var serviceBound = false
    
    private val _activeCombat = MutableStateFlow<Combat?>(null)
    val activeCombat: StateFlow<Combat?> = _activeCombat
    
    private val _combatLog = MutableStateFlow<List<String>>(emptyList())
    val combatLog: StateFlow<List<String>> = _combatLog
    
    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing
    
    private val _recentCombats = MutableStateFlow<List<CombatSummary>>(emptyList())
    val recentCombats: StateFlow<List<CombatSummary>> = _recentCombats
    
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as CombatService.CombatBinder
            combatService = binder.getService()
            serviceBound = true
            
            // Observar mudanças do serviço
            viewModelScope.launch {
                combatService?.activeCombat?.collect { combat ->
                    _activeCombat.value = combat
                }
            }
            
            viewModelScope.launch {
                combatService?.combatLog?.collect { log ->
                    _combatLog.value = log
                }
            }
            
            viewModelScope.launch {
                combatService?.isProcessing?.collect { processing ->
                    _isProcessing.value = processing
                }
            }
        }
        
        override fun onServiceDisconnected(name: ComponentName?) {
            combatService = null
            serviceBound = false
        }
    }
    
    fun bindCombatService(context: Context) {
        val intent = Intent(context, CombatService::class.java)
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }
    
    fun unbindCombatService(context: Context) {
        if (serviceBound) {
            context.unbindService(serviceConnection)
            serviceBound = false
        }
    }
    
    /**
     * Inicia um novo combate
     */
    fun startNewCombat(
        character: Character,
        difficulty: CombatDifficulty = CombatDifficulty.MEDIUM
    ) {
        combatService?.startNewCombat(character, difficulty) { combat ->
            _activeCombat.value = combat
        }
    }
    
    /**
     * Executa combate automaticamente
     */
    fun runAutoCombat(delayBetweenRounds: Long = 2000L) {
        val combat = _activeCombat.value ?: return
        
        combatService?.runAutoCombat(
            combat = combat,
            delayBetweenRounds = delayBetweenRounds,
            onRoundComplete = { round, messages ->
                // Atualizado via StateFlow
            },
            onCombatComplete = { completedCombat ->
                viewModelScope.launch {
                    saveCombatToDatabase(completedCombat)
                    loadRecentCombats()
                }
            }
        )
    }
    
    /**
     * Processa próxima rodada manualmente
     */
    fun processNextRound() {
        val combat = _activeCombat.value ?: return
        
        combatService?.processNextRound(combat) { round, messages ->
            if (combat.isFinished()) {
                viewModelScope.launch {
                    saveCombatToDatabase(combat)
                    loadRecentCombats()
                }
            }
        }
    }
    
    /**
     * Salva combate no banco de dados
     */
    private suspend fun saveCombatToDatabase(combat: Combat) {
        try {
            val combatEntity = com.olddragon.charactercreator.data.combat.CombatEntity(
                combatId = combat.id,
                playerCharacterId = combat.playerCharacterId,
                state = combat.state.name,
                currentRound = combat.currentRound,
                playerSurprised = combat.playerSurprised,
                enemySurprised = combat.enemySurprised,
                winnerId = combat.winnerId,
                startTime = combat.startTime,
                endTime = combat.endTime
            )
            
            combatDao.insertCombat(combatEntity)
            
            // Salvar combatentes
            combat.combatants.forEach { combatant ->
                val combatantEntity = com.olddragon.charactercreator.data.combat.CombatCombatantEntity(
                    combatId = combat.id,
                    combatantId = combatant.id,
                    name = combatant.name,
                    isPlayer = combatant.isPlayer,
                    initialHp = combatant.maxHp,
                    finalHp = combatant.currentHp,
                    ca = combatant.ca,
                    bac = combatant.bac,
                    bad = combatant.bad,
                    isDead = combatant.isDead
                )
                combatDao.insertCombatant(combatantEntity)
            }
            
            // Salvar ações (simplificado)
            combat.rounds.forEach { round ->
                round.actions.forEach { action ->
                    val actionEntity = com.olddragon.charactercreator.data.combat.CombatActionEntity(
                        combatId = combat.id,
                        roundNumber = round.roundNumber,
                        actionType = when (action) {
                            is CombatAction.Attack -> "ATTACK"
                            is CombatAction.Movement -> "MOVEMENT"
                            is CombatAction.Death -> "DEATH"
                            is CombatAction.AgonizeTest -> "AGONIZE"
                            is CombatAction.MoraleTest -> "MORALE"
                        },
                        actorId = when (action) {
                            is CombatAction.Attack -> action.attackerId
                            is CombatAction.Movement -> action.combatantId
                            is CombatAction.Death -> action.combatantId
                            is CombatAction.AgonizeTest -> action.combatantId
                            is CombatAction.MoraleTest -> "team"
                        },
                        targetId = when (action) {
                            is CombatAction.Attack -> action.targetId
                            else -> null
                        },
                        actionData = "" // Pode serializar JSON aqui
                    )
                    combatDao.insertAction(actionEntity)
                }
            }
            
        } catch (e: Exception) {
            android.util.Log.e("CombatViewModel", "Erro ao salvar combate", e)
        }
    }
    
    /**
     * Carrega combates recentes
     */
    fun loadRecentCombats() {
        viewModelScope.launch {
            try {
                val combats = combatDao.getRecentCombats()
                val summaries = combats.map { entity ->
                    val combatants = combatDao.getCombatantsByCombatId(entity.combatId)
                    val player = combatants.firstOrNull { it.isPlayer }
                    val enemy = combatants.firstOrNull { !it.isPlayer }
                    
                    CombatSummary(
                        id = entity.id,
                        combatId = entity.combatId,
                        playerName = player?.name ?: "Desconhecido",
                        enemyName = enemy?.name ?: "Inimigo",
                        rounds = entity.currentRound,
                        playerWon = entity.winnerId == player?.combatantId,
                        date = entity.startTime
                    )
                }
                _recentCombats.value = summaries
            } catch (e: Exception) {
                android.util.Log.e("CombatViewModel", "Erro ao carregar combates", e)
            }
        }
    }
    
    /**
     * Limpa combate atual
     */
    fun clearCombat() {
        combatService?.clearCombat()
        _activeCombat.value = null
        _combatLog.value = emptyList()
    }
    
    override fun onCleared() {
        super.onCleared()
        combatService = null
    }
}

data class CombatSummary(
    val id: Long,
    val combatId: String,
    val playerName: String,
    val enemyName: String,
    val rounds: Int,
    val playerWon: Boolean,
    val date: Long
)
