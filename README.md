# Chess

A two-player chess game with a Java Swing GUI, playable Human vs Human or
Human vs a minimax-based AI. Originally built as the term project for
3p71 (Software Engineering / AI concepts) by Francis Monwe and Jashandeep
Pannu; since polished up for a portfolio.

## Features

- **Human vs Human** — pass-and-play on one board.
- **Human vs AI** — play White against a computer opponent as Black, with
  three difficulty levels (Easy / Normal / Hard) controlling how many plies
  ahead the AI searches.
- Full standard chess rules: legal move generation per piece, check and
  checkmate detection, castling (kingside and queenside), en passant, and
  pawn promotion (choice of Queen/Rook/Bishop/Knight for the human player;
  the AI promotes automatically, biased toward a Queen at higher
  difficulties).
- Move highlighting: selecting a piece highlights its legal destinations
  (yellow) and any capturable enemy pieces (red); a king in check is
  highlighted red.

## How the AI works

The AI (`com.chess.game.AI`) always plays Black and picks moves with
**minimax search + alpha-beta pruning**:

- At each node, it enumerates every legal move for the side to move (using
  the same rules engine, `Piece`, that validates human moves) and recurses,
  alternating between maximizing (Black, the AI) and minimizing (White) the
  board evaluation.
- **Alpha-beta pruning** skips branches that can't change the final decision:
  once the opponent already has a better alternative elsewhere in the tree,
  there's no need to keep exploring a branch that can only get worse for
  them. This lets the search go deeper than plain minimax in the same time.
- Positions are scored with a **material + piece-square-table** heuristic:
  each piece contributes its standard material value (pawn 100, knight 320,
  bishop 330, rook 500, queen 900, king 20000) plus a positional bonus/malus
  from a per-piece-type 8x8 table (e.g. knights are penalized on the rim and
  rewarded near the centre; the king is penalized for wandering out of its
  back-rank shelter). A forced checkmate is scored as ±infinity so it always
  outranks a merely-good material score.
- **Difficulty** is just search depth: Easy searches 1 ply ahead, Normal 3,
  Hard 4.

## Project layout

Standard Maven layout:

```
src/main/java/com/chess/game/
    Chess.java   — entry point / mode & difficulty prompt
    Board.java   — Swing GUI, mouse input, game loop
    Piece.java   — board representation and move/check/checkmate rules
    AI.java      — minimax + alpha-beta search and evaluation
src/main/resources/images/
    *.png        — piece artwork, bundled into the jar as classpath resources
pom.xml
```

## Running it

### Option 1: download and run the prebuilt jar

Grab `Chess.jar` from the [Releases](../../releases) page (or wherever it
was shared with you) and either double-click it, or from a terminal:

```
java -jar Chess.jar
```

Requires a Java 17+ runtime (JRE) installed. No repo clone, no separate
image folder — the jar is fully self-contained.

### Option 2: build from source

Requires JDK 17+ and Maven.

```
git clone <this repo>
cd Chess-
mvn package
java -jar target/Chess.jar
```

`mvn package` compiles the sources and bundles the resources (piece images)
into a single runnable `target/Chess.jar`.

## Playing

1. Launch the jar. Choose **Human vs AI** or **Human vs Human**.
2. If Human vs AI, choose a difficulty (Easy/Normal/Hard) — you play White,
   the AI plays Black.
3. Click a piece to select it (legal moves highlight), then click a
   highlighted square to move there.
