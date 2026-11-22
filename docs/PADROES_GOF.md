# Padrões GoF no Colapso 2071 (Fase 3)

## Já presentes no código (basta nomear e mostrar)
| Padrão | Onde | Papel |
|---|---|---|
| Composite | `model/Condicao` | Condições compostas (E/OU/NÃO) formam uma árvore avaliada como uma só |
| Strategy | `model/Condicao`, `Consequencia` | Regra de disponibilidade/efeito de uma opção é trocável |
| Memento | `persistencia/SaveSerializador` | Salva só ids e reconstrói o `Jogo` a partir de `Rotas` |
| State | `model/EstadoJogo` | Comportamento muda conforme o estado da partida |
| Facade | `Aplicacao.criar` | Uma chamada monta Model, repositórios, views e controllers |
| Builder (fluente) | `model/Cena` | Montagem encadeada de cenas e opções |

## Aplicados no menu JavaFX
| Padrão | Onde | Papel |
|---|---|---|
| Command | `fx/AcaoMenu` | Cada opção do menu é um objeto (rótulo, atalho, `executar`); o menu não conhece as ações |
| Observer | `fx/TelaConfig` | Properties do JavaFX + listeners gravam cada mudança de configuração |
| Factory Method (simples) | `fx/Componentes` | Cria botões e rótulos com o estilo da identidade |
| Facade | `fx/ContextoFx`, `fx/Navegador` | Esconde repositórios e troca de telas |

## Sugestões para as próximas telas
- **Observer**: ligar recursos (água, comida, energia) a `IntegerProperty` e a barras de progresso da cena.
- **Template Method**: uma tela base com `construir()` e ganchos para título, rodapé e botão Voltar.
- **Singleton**: não recomendado; prefira passar `ContextoFx` (injeção), que já facilita testes.
