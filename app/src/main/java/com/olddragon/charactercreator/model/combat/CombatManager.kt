package com.olddragon.charactercreator.model.combat

class CombatManager(private val engine: CombatEngine = CombatEngine()) {
    
    /**
     * Inicia um novo combate
     */
    fun startCombat(
        playerCombatant: Combatant,
        enemies: List<Combatant>,
        combatId: String = "combat_${System.currentTimeMillis()}"
    ): Combat {
        val combat = Combat(
            id = combatId,
            playerCharacterId = playerCombatant.id.toLongOrNull() ?: 0L,
            state = CombatState.SURPRISE_CHECK
        )
        
        combat.combatants.add(playerCombatant)
        combat.combatants.addAll(enemies)
        
        return combat
    }
    
    /**
     * Processa verificação de surpresa
     */
    fun processSurpriseCheck(combat: Combat): List<String> {
        val messages = mutableListOf<String>()
        
        combat.playerSurprised = engine.checkSurprise(false)
        combat.enemySurprised = engine.checkSurprise(false)
        
        when {
            combat.playerSurprised && !combat.enemySurprised -> {
                messages.add("⚠️ Você foi pego de surpresa! Perde a primeira rodada.")
                combat.getPlayer()?.hasActedThisRound = true
            }
            !combat.playerSurprised && combat.enemySurprised -> {
                messages.add("✨ Você surpreendeu o inimigo! Ele perde a primeira rodada.")
                combat.getEnemies().forEach { it.hasActedThisRound = true }
            }
            combat.playerSurprised && combat.enemySurprised -> {
                messages.add("⚔️ Ambos os lados foram surpreendidos!")
            }
            else -> {
                messages.add("⚔️ Nenhum lado foi surpreendido. O combate começa!")
            }
        }
        
        combat.state = CombatState.INITIATIVE_ROLL
        return messages
    }
    
    /**
     * Processa rolagem de iniciativa
     */
    fun processInitiativeRoll(combat: Combat): List<String> {
        val messages = mutableListOf<String>()
        messages.add("\n🎲 Rolando Iniciativa...")
        
        combat.combatants.forEach { combatant ->
            if (!combatant.hasActedThisRound) {
                val success = engine.rollInitiative(combatant)
                val result = if (success) "✓ Sucesso" else "✗ Falha"
                messages.add("  ${combatant.name}: ${combatant.initiative} $result")
            }
        }
        
        combat.state = CombatState.IN_PROGRESS
        return messages
    }
    
    /**
     * Processa uma rodada completa de combate
     */
    fun processRound(combat: Combat): Pair<CombatRound, List<String>> {
        val messages = mutableListOf<String>()
        val actions = mutableListOf<CombatAction>()
        
        combat.currentRound++
        messages.add("\n═══════════════════════════════")
        messages.add("⚔️  RODADA ${combat.currentRound}")
        messages.add("═══════════════════════════════\n")
        
        // Reset para nova rodada
        combat.combatants.forEach { it.resetForNewRound() }
        
        // Determinar ordem de ação
        val actionOrder = engine.determineActionOrder(combat.combatants.filter { it.isAlive() })
        
        messages.add("📋 Ordem de Ação:")
        actionOrder.forEachIndexed { index, combatant ->
            messages.add("  ${index + 1}. ${combatant.name} (${if (combatant.isPlayer) "Jogador" else "Inimigo"})")
        }
        messages.add("")
        
        // Executar ações
        for (combatant in actionOrder) {
            if (!combatant.canAct()) continue
            
            messages.add("┌─ ${combatant.name} (HP: ${combatant.currentHp}/${combatant.maxHp})")
            
            // Determinar alvo
            val allies = if (combatant.isPlayer) {
                combat.getAliveAllies()
            } else {
                combat.getAliveEnemies()
            }
            
            val enemies = if (combatant.isPlayer) {
                combat.getAliveEnemies()
            } else {
                combat.getAliveAllies()
            }
            
            val targetId = if (combatant.isPlayer) {
                enemies.firstOrNull()?.id ?: ""
            } else {
                engine.decideEnemyAction(combatant, enemies, allies)
            }
            
            val target = combat.combatants.firstOrNull { it.id == targetId }
            
            if (target != null && target.isAlive()) {
                // Ataque
                val attackAction = engine.performAttack(combatant, target, isMeleeAttack = true)
                actions.add(attackAction)
                
                messages.add("│  🗡️  Ataca ${target.name}")
                messages.add("│  🎲 Rolou ${attackAction.attackRoll} + ${combatant.bac} = ${attackAction.totalAttack} vs CA ${attackAction.targetCA}")
                
                when {
                    attackAction.isCritical -> {
                        messages.add("│  💥 CRÍTICO! Dano: ${attackAction.damage} (${attackAction.damageRoll})")
                        messages.add("│  ❤️  ${target.name}: ${target.currentHp}/${target.maxHp} HP")
                    }
                    attackAction.isFumble -> {
                        messages.add("│  💔 ERRO CRÍTICO! Ataque falhou completamente!")
                    }
                    attackAction.isHit -> {
                        messages.add("│  ✓ ACERTOU! Dano: ${attackAction.damage} (${attackAction.damageRoll})")
                        messages.add("│  ❤️  ${target.name}: ${target.currentHp}/${target.maxHp} HP")
                    }
                    else -> {
                        messages.add("│  ✗ ERROU! O ataque não acertou.")
                    }
                }
                
                if (target.isDying) {
                    messages.add("│  ⚠️  ${target.name} está MORRENDO!")
                }
                
                // Movimento
                val movementAction = engine.performMovement(combatant, 3)
                actions.add(movementAction)
                messages.add("│  🏃 Move-se ${movementAction.distance}m")
            } else {
                messages.add("│  ⚠️  Nenhum alvo válido!")
            }
            
            combatant.hasActedThisRound = true
            messages.add("└─")
            messages.add("")
        }
        
        // Fim da rodada
        messages.add("═══════════════════════════════")
        messages.add("🔚 Fim da Rodada ${combat.currentRound}")
        messages.add("═══════════════════════════════\n")
        
        // Processar combatentes morrendo
        val dyingCombatants = combat.combatants.filter { it.isDying && !it.isDead }
        for (combatant in dyingCombatants) {
            messages.add("💀 ${combatant.name} está agonizando...")
            val agonizeAction = engine.performAgonizeTest(combatant)
            actions.add(agonizeAction)
            
            if (agonizeAction.succeeded) {
                messages.add("   ✓ Teste de Agonizar: ${agonizeAction.roll} ≤ ${agonizeAction.target} - ESTABILIZADO")
            } else {
                messages.add("   ✗ Teste de Agonizar: ${agonizeAction.roll} > ${agonizeAction.target} - MORREU")
                val deathAction = CombatAction.Death(combatant.id, combatant.name)
                actions.add(deathAction)
            }
        }
        
        // Verificar teste de moral
        val totalEnemies = combat.getEnemies().size
        val deadEnemies = combat.getEnemies().count { it.isDead }
        
        if (deadEnemies >= totalEnemies / 2 && combat.getAliveEnemies().isNotEmpty()) {
            messages.add("\n⚠️ Metade dos inimigos foi derrotada! Teste de Moral...")
            val moraleAction = engine.performMoraleTest(combat.getAliveEnemies())
            actions.add(moraleAction)
            
            if (!moraleAction.succeeded) {
                messages.add("   ✗ Teste de Moral falhou! Inimigos fogem!")
                combat.getAliveEnemies().forEach { 
                    it.isDead = true 
                    messages.add("   🏃 ${it.name} foge do combate!")
                }
            } else {
                messages.add("   ✓ Teste de Moral passou! Inimigos continuam lutando!")
            }
        }
        
        val round = CombatRound(
            roundNumber = combat.currentRound,
            actions = actions,
            combatantsState = combat.getCurrentSnapshot()
        )
        
        combat.addRound(round)
        
        if (engine.checkCombatEnd(combat)) {
            messages.add("\n🏆 COMBATE FINALIZADO!")
            val winner = combat.combatants.firstOrNull { it.id == combat.winnerId }
            if (winner != null) {
                messages.add("   Vencedor: ${winner.name}")
            }
        }
        
        return Pair(round, messages)
    }
    
    /**
     * Executa um combate completo automaticamente
     */
    fun runFullCombat(combat: Combat, maxRounds: Int = 20): List<String> {
        val allMessages = mutableListOf<String>()
        
        allMessages.addAll(processSurpriseCheck(combat))
        allMessages.addAll(processInitiativeRoll(combat))
        
        var roundCount = 0
        while (!combat.isFinished() && roundCount < maxRounds) {
            val (_, messages) = processRound(combat)
            allMessages.addAll(messages)
            roundCount++
        }
        
        if (roundCount >= maxRounds) {
            allMessages.add("\n⚠️ Combate excedeu o limite de rodadas!")
        }
        
        return allMessages
    }
    
    /**
     * Cria um combatente a partir de um Character
     */
    fun createCombatantFromCharacter(character: com.olddragon.charactercreator.model.Character): Combatant {
        return Combatant(
            id = character.id.toString(),
            name = character.name,
            isPlayer = true,
            currentHp = character.hp,
            maxHp = character.hp,
            ca = character.ca,
            bac = character.ba,
            bad = character.ba,
            jpc = character.jp,
            jps = character.jp,
            dexterity = character.attributes["Destreza"] ?: 10,
            wisdom = character.attributes["Sabedoria"] ?: 10,
            strength = character.attributes["Força"] ?: 10,
            weaponDamage = getWeaponDamageByClass(character.characterClass.name),
            weaponName = getWeaponNameByClass(character.characterClass.name),
            movement = character.movement,
            currentPosition = 0
        )
    }
    
    private fun getWeaponDamageByClass(className: String): String {
        return when (className) {
            "Guerreiro" -> "1d8+2"
            "Clérigo" -> "1d6+1"
            "Ladrão" -> "1d6"
            else -> "1d6"
        }
    }
    
    private fun getWeaponNameByClass(className: String): String {
        return when (className) {
            "Guerreiro" -> "Espada Longa"
            "Clérigo" -> "Maça"
            "Ladrão" -> "Adaga"
            else -> "Arma"
        }
    }
}
