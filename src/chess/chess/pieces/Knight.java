package chess.chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessPiece;
import chess.Color;

public class Knight extends ChessPiece {
    public Knight(Board board, Color color) {
        super(board, color);
    }

    @Override
    public String toString(){
        return "N";
    }

    private boolean canMove(Position position){
        ChessPiece piece = (ChessPiece)getBoard().piece(position);
        return piece == null || piece.getColor() != this.getColor();
    }

    @Override
    public boolean[][] possibleMoves() {
        boolean[][] mat = new boolean[getBoard().getRows()][getBoard().getColumns()];

        Position p = new Position(0, 0);

        //L invertido pra a direita
        p.setValues(position.getRow() - 2, position.getColumn()+1);
        if (getBoard().positionExists(p) && this.canMove(p)){
            mat[p.getRow()][p.getColumn()] = true;
        }
        //L invertido pra a esquerda
        p.setValues(position.getRow() - 2, position.getColumn() - 1);
        if (getBoard().positionExists(p) && this.canMove(p)){
            mat[p.getRow()][p.getColumn()] = true;
        }
        //L pra a direita
        p.setValues(position.getRow() + 2 , position.getColumn() + 1);
        if (getBoard().positionExists(p) && this.canMove(p)){
            mat[p.getRow()][p.getColumn()] = true;
        }
        //L pra a esquerda
        p.setValues(position.getRow() + 2, position.getColumn() - 1);
        if (getBoard().positionExists(p) && this.canMove(p)){
            mat[p.getRow()][p.getColumn()] = true;
        }
        //L deitado pra a direita e pra cima
        p.setValues(position.getRow() - 1, position.getColumn() + 2);
        if (getBoard().positionExists(p) && this.canMove(p)){
            mat[p.getRow()][p.getColumn()] = true;
        }
        //L deitado pra a direita e pra baixo
        p.setValues(position.getRow() + 1, position.getColumn() + 2);
        if (getBoard().positionExists(p) && this.canMove(p)){
            mat[p.getRow()][p.getColumn()] = true;
        }
        //L deitado pra a esquerda e pra cima
        p.setValues(position.getRow() - 1, position.getColumn() - 2);
        if (getBoard().positionExists(p) && this.canMove(p)){
            mat[p.getRow()][p.getColumn()] = true;
        }
        //L deitado pra a esquerda e pra baixo
        p.setValues(position.getRow() + 1, position.getColumn() - 2);
        if (getBoard().positionExists(p) && this.canMove(p)){
            mat[p.getRow()][p.getColumn()] = true;
        }

        return mat;
    }
}
