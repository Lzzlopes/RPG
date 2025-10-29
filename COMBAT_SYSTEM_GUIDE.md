# 🗡️ Sistema de Combate - Old Dragon

## Visão Geral

O sistema de combate implementa as regras completas do Old Dragon 2 para combates automáticos entre personagens e inimigos gerados aleatoriamente, executando em segundo plano através de um serviço Android.

## 📁 Estrutura de Arquivos

```
model/combat/
├── Combatant.kt              # Representação de um combatente
├── Combat.kt                  # Estado do combate
├── CombatAction.kt            # Ações de combate (Attack, Movement, etc)
├── CombatRound.kt             # Rodada de combate
├── CombatEngine.kt            # Motor de combate (regras e dados)
├── CombatManager.kt           # Gerenciador de alto nível
└── EnemyGenerator.kt          # Gerador de inimigos aleatórios

data/combat/
├── CombatEntities.kt          # Entidades Room para persistência
└── CombatDao.kt               # DAO para operações no banco

service/
└── CombatService.kt           # Serviço Android para execução em segundo plano

controller/
└── CombatViewModel.kt         # ViewModel para gerenciar combates na UI
```

## ⚔️ Fluxo de Combate

### 1. Inicialização
```kotlin
val playerCombatant = combatManager.createCombatantFromCharacter(character)
val enemy = EnemyGenerator.generateEnemyByDifficulty(CombatDifficulty.MEDIUM)
val combat = combatManager.startCombat(playerCombatant, listOf(enemy))
```

### 2. Verificação de Surpresa
- Rola 1d6: 1-2 = surpreendido
- Lado surpreendido perde primeira rodada

### 3. Iniciativa  
- 1d20 vs Destreza/Sabedoria (maior)
- Ordem: Sucessos → Inimigos → Falhas

### 4. Rodada: Ataque + Movimento
- Ataque: 1d20 + BAC vs CA
- Crítico (20): dano x2
- Erro (1): sempre erra

### 5. Fim da Rodada
- Teste Agonizar (HP ≤ 0)
- Teste Moral (50%+ mortos)

## 🎮 Uso do Sistema

### Via ViewModel (Recomendado)
```kotlin
// 1. Bind ao serviço
viewModel.bindCombatService(context)

// 2. Iniciar combate
viewModel.startNewCombat(character, CombatDifficulty.MEDIUM)

// 3. Executar automaticamente
viewModel.runAutoCombat(delayBetweenRounds = 2000L)

// 4. Observar
viewModel.activeCombat.collect { combat -> }
viewModel.combatLog.collect { log -> }
```

### Via CombatManager (Direto)
```kotlin
val manager = CombatManager()
val combat = manager.startCombat(player, enemies)
val messages = manager.runFullCombat(combat)
```

## 🤖 Inimigos Disponíveis

1. **Goblin** (Fácil): HP 4-8, CA 12
2. **Orc** (Médio): HP 8-12, CA 14
3. **Esqueleto** (Médio): HP 6-10, CA 13
4. **Bandido** (Médio): HP 7-11, CA 13
5. **Gnoll** (Difícil): HP 10-14, CA 15
6. **Lobo** (Médio): HP 8-12, CA 14
7. **Zumbi** (Difícil): HP 12-16, CA 12

```kotlin
// Gerar
val enemy = EnemyGenerator.generateEnemyByDifficulty(CombatDifficulty.MEDIUM)
```

## 📊 Exemplo de Log

```
⚔️ Nenhum lado foi surpreendido!

🎲 Rolando Iniciativa...
  Asta: 15 ✓ Sucesso
  Goblin: 8 ✗ Falha

═══════════════════════════════
⚔️  RODADA 1
═══════════════════════════════

┌─ Asta (HP: 10/10)
│  🗡️  Ataca Goblin
│  🎲 Rolou 18 + 4 = 22 vs CA 12
│  ✓ ACERTOU! Dano: 7 (5+2)
│  ❤️  Goblin: 1/6 HP
└─

┌─ Goblin (HP: 1/6)
│  🗡️  Ataca Asta
│  🎲 Rolou 3 + 2 = 5 vs CA 16
│  ✗ ERROU!
└─

🏆 COMBATE FINALIZADO!
   Vencedor: Asta
```

## 💾 Persistência

Combates são salvos automaticamente no banco de dados Room ao finalizar:
- Informações do combate
- Combatentes e status final
- Todas as ações por rodada

```kotlin
// Carregar histórico
viewModel.loadRecentCombats()
viewModel.recentCombats.collect { combats -> }
```

## ⚙️ Customização

### Adicionar Novo Inimigo
Em `EnemyGenerator.kt`:
```kotlin
EnemyTemplate(
    name = "Novo Inimigo",
    hpRange = 10..15,
    ca = 14,
    bac = 4,
    // ... outros atributos
)
```

### Modificar IA
Em `CombatEngine.kt` → `decideEnemyAction()`:
```kotlin
// Atacar o mais fraco
return allies.minByOrNull { it.currentHp }?.id

// Atacar aleatório  
return allies.randomOrNull()?.id
```

## 🔄 Ciclo de Vida do Serviço

```kotlin
onCreate() {
    viewModel.bindCombatService(context)
}

onDestroy() {
    viewModel.unbindCombatService(context)
}
```

## 📱 AndroidManifest

```xml
<service
    android:name=".service.CombatService"
    android:enabled="true"
    android:exported="false" />
```

## 🧪 Testing

```kotlin
@Test
fun testCombatFlow() {
    val manager = CombatManager()
    val player = createTestPlayer()
    val enemy = createTestEnemy()
    
    val combat = manager.startCombat(player, listOf(enemy))
    val messages = manager.runFullCombat(combat)
    
    assertTrue(combat.isFinished())
    assertNotNull(combat.winnerId)
}
```

## 📈 Próximas Features

- [ ] Múltiplos inimigos simultâneos
- [ ] Sistema de magias
- [ ] Efeitos de status
- [ ] Combate PvP
- [ ] Replay de combates
- [ ] Animações visuais

---

**"Surpass your limits!" 🐂⚔️**
