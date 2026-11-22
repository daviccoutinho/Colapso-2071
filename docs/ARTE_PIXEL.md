# Arte pixelada

Estilo: sertão aconchegante e quente, contorno marrom, sombreado suave, água como única cor fria.
Desenho original deste projeto; nenhum sprite, personagem ou paleta foi copiado de outro jogo.

## Regras
- Cenários em 320×180 px, mostrados em 4× (1280×720).
- Sem suavização: no JavaFX, `ImageView.setSmooth(false)` e ampliação em múltiplos inteiros.
- Luz de cima. Contorno de 1 px em tom escuro quente.

## Onde estão
Todos os PNGs ficam em `src/main/resources/br/colapso2071/fx/img/`; `fx/Arte` liga cada personagem, item e lugar ao
seu arquivo.

| Arquivos | Tamanho | Uso |
|---|---|---|
| `cena_*.png` (10) | 320×180, 4× | cenários: menu, chegada, praça, poço, celeiro, casa, quintal, depósito e os finais |
| `ret_*.png` | 64×64, 3× | retrato de quem fala, num quadro de madeira à direita da caixa de texto; variantes `_fala` (boca aberta) e `_pisca` |
| `fig_*.png` | 16×32, 4× | personagens de corpo inteiro na cena, de frente; variante `_pisca`. O Caramelo tem 24×18 (em pé, latindo e sentado) |
| `item_*.png` | 32×32 | itens da mochila |
| `ico_*.png`, `botao_*.png`, `slot_*.png`, `painel.png`, `logo.png` | vários | interface |

O ícone do programa (Caramelo no pôr do sol) fica em `arte/icone/` em PNG, ICO (Windows) e ICNS (macOS), usado pelo
`Empacotar.java`.

## Na cena
- `Arte.PISO_Y = 127` é a linha do chão em todos os cenários; `TelaCena` põe os pés dos personagens nela, então
  ninguém flutua. Os testes conferem tamanho e linha dos pés de cada sprite.
- Os personagens respiram (sobem 2 px devagar), piscam de vez em quando e mexem a boca enquanto o texto aparece.
