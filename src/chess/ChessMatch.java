package chess;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.chess.pieces.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChessMatch {

    private int turn;
    private Color currentPlayer;
    private Board board;
    private boolean check;
    private boolean checkMate;
    private ChessPiece enPassantVulnerable;
    private ChessPiece promoted;

    private List<Piece> piecesOnTheBoard = new ArrayList<>();
    private List<Piece> capturedPieces = new ArrayList<>();

    public ChessMatch() {
        this.board = new Board(8, 8);
        turn = 1;
        currentPlayer = Color.WHITE;
        checkMate = false;
        this.initialSetup();
    }

    public int getTurn() {
        return turn;
    }
    public Color getCurrentPlayer() {
        return currentPlayer;
    }
    public boolean getCheck(){
        return check;
    }

    public boolean getCheckMate() {
        return checkMate;
    }

    public ChessPiece getEnPassantVulnerable() {
        return enPassantVulnerable;
    }

    public ChessPiece getPromoted() {
        return promoted;
    }

    public ChessPiece[][] getPieces(){
        ChessPiece[][] mat = new ChessPiece[board.getRows()][board.getColumns()];
        for (int i= 0; i< board.getRows(); i++){
            for (int j=0; j< board.getColumns(); j++){
                mat[i][j] = (ChessPiece) board.piece(i, j);
            }
        }
        return mat;
    }

    public boolean[][] possibleMoves(ChessPosition sourcePosition){
        Position position = sourcePosition.toPosition();
        this.validateSourcePosition(position);
        return board.piece(position).possibleMoves();
    }

    public ChessPiece performChessMove(ChessPosition sourcePosition, ChessPosition targetPosition){
        Position source = sourcePosition.toPosition();
        Position target =targetPosition.toPosition();
        this.validateSourcePosition(source);
        this.validateTargetPosition(source, target);
        Piece capturedPiece = makeMove(source, target);

        if(testCheck(currentPlayer)){
            undoMove(source, target, capturedPiece);
            throw new ChessException("Voce nao pode se colocar em cheque. Esse movimento nao eh permitido");
        }

        ChessPiece movedPiece = (ChessPiece)board.piece(target);

        //Movimento especial promoção
        promoted = null;
        if (movedPiece instanceof Pawn){
            if ((movedPiece.getColor() == Color.WHITE && target.getRow() == 0)||(movedPiece.getColor() == Color.BLACK && target.getRow() == 7)){
                promoted = (ChessPiece) board.piece(target);
                promoted = replacePromotedPiece("Q");
            }
        }

        check = (testCheck(opponent(currentPlayer)))? true : false;

        if (testCheckMate(opponent(currentPlayer))){
            checkMate = true;
        }
        else {
            nextTurn();
        }

        //Movimento especial en passant
        if (movedPiece instanceof Pawn && (target.getRow() == source.getRow() - 2 || target.getRow() == source.getRow() + 2)){
            enPassantVulnerable = movedPiece;
        }
        else {
            enPassantVulnerable = null;
        }

        return (ChessPiece) capturedPiece;
    }

    public ChessPiece replacePromotedPiece(String type){
        if (promoted == null){
            throw new IllegalStateException("Nao ha peca para ser promovida.");
        }
        if (!type.equals("Q") && !type.equals("B") && !type.equals("N") && !type.equals("R")){
            return promoted;
        }

        Position position = promoted.getChessPosition().toPosition();
        Piece piece = board.removePiece(position);
        piecesOnTheBoard.remove(piece);

        ChessPiece newPiece = newPiece(type, promoted.getColor());
        board.placePiece(newPiece, position);
        piecesOnTheBoard.add(newPiece);

        return newPiece;
    }

    private ChessPiece newPiece(String type, Color color){
        if (type.equals("Q")){
            return new Queen(board, color);}
        if (type.equals("B")){
            return new Bishop(board, color);}
        if (type.equals("N")){
            return new Knight(board, color);}
        return new Rook(board, color);
    }

    private Piece makeMove(Position source, Position target){
        ChessPiece piece = (ChessPiece) board.removePiece(source);
        piece.increaseMoveCount();
        Piece capturedPiece = board.removePiece(target);
        board.placePiece(piece, target);

        if (capturedPiece != null){
            piecesOnTheBoard.remove(capturedPiece);
            capturedPieces.add(capturedPiece);
        }

        //Movimento Especial Roque Pequeno
        if (piece instanceof King && target.getColumn() == source.getColumn() + 2){
            Position sourceTorre = new Position(source.getRow(), source.getColumn() + 3);
            Position targetTorre = new Position(source.getRow(), source.getColumn() + 1);
            ChessPiece rook = (ChessPiece) board.removePiece(sourceTorre);
            board.placePiece(rook, targetTorre);
            rook.increaseMoveCount();
        }
        //Movimento Especial Roque Grande
        if (piece instanceof King && target.getColumn() == source.getColumn() - 2){
            Position sourceTorre = new Position(source.getRow(), source.getColumn() - 4);
            Position targetTorre = new Position(source.getRow(), source.getColumn() - 1);
            ChessPiece rook = (ChessPiece) board.removePiece(sourceTorre);
            board.placePiece(rook, targetTorre);
            rook.increaseMoveCount();
        }

        //Movimento Especial En Passant
        if (piece instanceof Pawn){
            if (source.getColumn() != target.getColumn() && capturedPiece == null){
                Position pawnPosition;
                if (piece.getColor()== Color.WHITE){
                    pawnPosition = new Position(target.getRow() + 1, target.getColumn());
                }
                else {
                    pawnPosition = new Position(target.getRow() - 1, target.getColumn());
                }
                capturedPiece = board.removePiece(pawnPosition);
                capturedPieces.add(capturedPiece);
                piecesOnTheBoard.remove(capturedPiece);
            }
        }

        return capturedPiece;
    }

    private void undoMove(Position source, Position target, Piece capturedPiece){
        ChessPiece piece = (ChessPiece) board.removePiece(target);
        piece.decreaseMoveCount();
        board.placePiece(piece, source);

        if (capturedPiece != null){
            board.placePiece(capturedPiece, target);
            capturedPieces.remove(capturedPiece);
            piecesOnTheBoard.add(capturedPiece);
        }

        //Desfazer Movimento Especial Roque Pequeno
        if (piece instanceof King && target.getColumn() == source.getColumn() + 2){
            Position sourceTorre = new Position(source.getRow(), source.getColumn() + 3);
            Position targetTorre = new Position(source.getRow(), source.getColumn() + 1);
            ChessPiece rook = (ChessPiece) board.removePiece(targetTorre);
            board.placePiece(rook, sourceTorre);
            rook.decreaseMoveCount();
        }
        //Desfazer Movimento Especial Roque Grande
        if (piece instanceof King && target.getColumn() == source.getColumn() - 2){
            Position sourceTorre = new Position(source.getRow(), source.getColumn() - 4);
            Position targetTorre = new Position(source.getRow(), source.getColumn() - 1);
            ChessPiece rook = (ChessPiece) board.removePiece(targetTorre);
            board.placePiece(rook, sourceTorre);
            rook.decreaseMoveCount();
        }

        //Movimento Especial En Passant
        if (piece instanceof Pawn){
            if (source.getColumn() != target.getColumn() && capturedPiece == enPassantVulnerable){
                ChessPiece pawn =(ChessPiece) board.removePiece(target);
                Position pawnPosition;
                if (piece.getColor()== Color.WHITE){
                    pawnPosition = new Position(3, target.getColumn());
                }
                else {
                    pawnPosition = new Position(4, target.getColumn());
                }
                board.placePiece(pawn, pawnPosition);
            }
        }
    }

    private void validateSourcePosition(Position position){
        if (!board.thereIsAPiece(position)){
            throw new ChessException("ERRO: Nao existe peca na posicao de origem.");
        }
        if (currentPlayer !=  ((ChessPiece)board.piece(position)).getColor()){
            throw new ChessException("Essa peca nao eh sua!");
        }
        if (!board.piece(position).isThereAnyPossibleMove()){
            throw new ChessException("Nao ha movimentos possiveis para a peca escolhida.");
        }
    }

    private void validateTargetPosition(Position source, Position target){
        if (!board.piece(source).possibleMove(target)){
            throw new ChessException("A peca escolhida nao pode ir para o local escolhido.");
        }
    }

    private void nextTurn(){
        turn++;
        currentPlayer = (currentPlayer == Color.WHITE)? Color.BLACK : Color.WHITE;
    }

    private Color opponent(Color color){
        return (color == Color.WHITE)? Color.BLACK : Color.WHITE;
    }

    private ChessPiece king(Color color){
        List<Piece> myList = piecesOnTheBoard.stream().filter( x -> ((ChessPiece)x).getColor() == color).collect(Collectors.toList());
        for (Piece piece : myList){
            if (piece instanceof King){
                return (ChessPiece) piece;
            }
        }
        throw new IllegalStateException("ERRO: Nao ha um Rei " + color + " no tabuleiro.");
    }

    private boolean testCheck(Color color){
        Position kingPosition = king(color).getChessPosition().toPosition();
        List<Piece> opponentPieces = piecesOnTheBoard.stream().filter( x -> ((ChessPiece)x).getColor() == opponent(color)).collect(Collectors.toList());
        for (Piece p : opponentPieces){
            boolean[][] mat = p.possibleMoves();
            if (mat[kingPosition.getRow()][kingPosition.getColumn()]){
                return true;
            }
        }
        return false;
    }

    private boolean testCheckMate(Color color){
        if (!testCheck(color)){
            return false;
        }
        List<Piece> myList = piecesOnTheBoard.stream().filter( x -> ((ChessPiece)x).getColor() == color).collect(Collectors.toList());
        for (Piece p : myList){
            boolean[][] mat = p.possibleMoves();
            for (int i= 0; i< board.getRows(); i++){
                for (int j = 0; j< board.getColumns(); j++){
                    if (mat[i][j]){
                        Position source = ((ChessPiece)p).getChessPosition().toPosition();
                        Position target = new Position(i, j);
                        Piece capturedPiece = makeMove(source, target);
                        boolean testCheck = testCheck(color);
                        undoMove(source, target, capturedPiece);
                        if (!testCheck){
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    private void placeNewPiece(char column, int row, ChessPiece piece){
        board.placePiece(piece, new ChessPosition(column, row).toPosition());
        piecesOnTheBoard.add(piece);
    }
    private void initialSetup(){
        //Lado Branco
        this.placeNewPiece('e', 1, new King(board, Color.WHITE, this));
        this.placeNewPiece('d', 1, new Queen(board, Color.WHITE));
        this.placeNewPiece('a', 2, new Pawn(board, Color.WHITE,this));
        this.placeNewPiece('b', 2, new Pawn(board, Color.WHITE,this));
        this.placeNewPiece('c', 2, new Pawn(board, Color.WHITE,this));
        this.placeNewPiece('d', 2, new Pawn(board, Color.WHITE,this));
        this.placeNewPiece('e', 2, new Pawn(board, Color.WHITE,this));
        this.placeNewPiece('f', 2, new Pawn(board, Color.WHITE,this));
        this.placeNewPiece('g', 2, new Pawn(board, Color.WHITE,this));
        this.placeNewPiece('h', 2, new Pawn(board, Color.WHITE,this));
        this.placeNewPiece('c', 1, new Bishop(board, Color.WHITE));
        this.placeNewPiece('f', 1, new Bishop(board, Color.WHITE));
        this.placeNewPiece('b', 1, new Knight(board, Color.WHITE));
        this.placeNewPiece('g', 1, new Knight(board, Color.WHITE));
        this.placeNewPiece('a', 1, new Rook(board, Color.WHITE));
        this.placeNewPiece('h', 1, new Rook(board, Color.WHITE));

        this.placeNewPiece('e', 8, new King(board, Color.BLACK, this));
        this.placeNewPiece('d', 8, new Queen(board, Color.BLACK));
        this.placeNewPiece('a', 7, new Pawn(board, Color.BLACK, this));
        this.placeNewPiece('b', 7, new Pawn(board, Color.BLACK, this));
        this.placeNewPiece('c', 7, new Pawn(board, Color.BLACK, this));
        this.placeNewPiece('d', 7, new Pawn(board, Color.BLACK, this));
        this.placeNewPiece('e', 7, new Pawn(board, Color.BLACK, this));
        this.placeNewPiece('f', 7, new Pawn(board, Color.BLACK, this));
        this.placeNewPiece('g', 7, new Pawn(board, Color.BLACK, this));
        this.placeNewPiece('h', 7, new Pawn(board, Color.BLACK, this));
        this.placeNewPiece('c', 8, new Bishop(board, Color.BLACK));
        this.placeNewPiece('f', 8, new Bishop(board, Color.BLACK));
        this.placeNewPiece('b', 8, new Knight(board, Color.BLACK));
        this.placeNewPiece('g', 8, new Knight(board, Color.BLACK));
        this.placeNewPiece('a', 8, new Rook(board, Color.BLACK));
        this.placeNewPiece('h', 8, new Rook(board, Color.BLACK));
    }

}
