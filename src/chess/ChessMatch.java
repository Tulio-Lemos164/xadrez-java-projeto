package chess;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.chess.pieces.King;
import chess.chess.pieces.Rook;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChessMatch {

    private int turn;
    private Color currentPlayer;
    private Board board;
    private boolean check;

    private List<Piece> piecesOnTheBoard = new ArrayList<>();
    private List<Piece> capturedPieces = new ArrayList<>();

    public ChessMatch() {
        this.board = new Board(8, 8);
        turn = 1;
        currentPlayer = Color.WHITE;
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

        check = (testCheck(opponent(currentPlayer)))? true : false;

        nextTurn();
        return (ChessPiece) capturedPiece;
    }

    private Piece makeMove(Position source, Position target){
        Piece piece = board.removePiece(source);
        Piece capturedPiece = board.removePiece(target);
        board.placePiece(piece, target);

        if (capturedPiece != null){
            piecesOnTheBoard.remove(capturedPiece);
            capturedPieces.add(capturedPiece);
        }

        return capturedPiece;
    }

    private void undoMove(Position source, Position target, Piece capturedPiece){
        Piece piece = board.removePiece(target);
        board.placePiece(piece, source);

        if (capturedPiece != null){
            board.placePiece(capturedPiece, target);
            capturedPieces.remove(capturedPiece);
            piecesOnTheBoard.add(capturedPiece);
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

    private void placeNewPiece(char column, int row, ChessPiece piece){
        board.placePiece(piece, new ChessPosition(column, row).toPosition());
        piecesOnTheBoard.add(piece);
    }
    private void initialSetup(){
        this.placeNewPiece('e', 1, new King(board, Color.WHITE));
        this.placeNewPiece('e', 8, new King(board, Color.BLACK));
        this.placeNewPiece('a', 1, new Rook(board, Color.WHITE));
        this.placeNewPiece('a', 8, new Rook(board, Color.BLACK));
    }

}
