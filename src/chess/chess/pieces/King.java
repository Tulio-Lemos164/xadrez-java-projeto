package chess.chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessPiece;
import chess.Color;

public class King extends ChessPiece {

    public King(Board board, Color color) {
        super(board, color);
    }

    @Override
    public String toString(){
        return "K";
    }

    private boolean canMove(Position position){
        ChessPiece piece = (ChessPiece)getBoard().piece(position);
        return piece == null || piece.getColor() != this.getColor();
    }

    @Override
    public boolean[][] possibleMoves() {
        boolean[][] mat = new boolean[getBoard().getRows()][getBoard().getColumns()];

        Position p = new Position(0, 0);

        //Casa Acima do Rei
        p.setValues(position.getRow() - 1, position.getColumn());
        if (getBoard().positionExists(p) && this.canMove(p)){
            mat[p.getRow()][p.getColumn()] = true;
        }
        //Casa à Esquerda do Rei
        p.setValues(position.getRow() , position.getColumn() - 1);
        if (getBoard().positionExists(p) && this.canMove(p)){
            mat[p.getRow()][p.getColumn()] = true;
        }
        //Casa à Direita do Rei
        p.setValues(position.getRow() , position.getColumn() + 1);
        if (getBoard().positionExists(p) && this.canMove(p)){
            mat[p.getRow()][p.getColumn()] = true;
        }
        //Casa Abaixo do Rei
        p.setValues(position.getRow() + 1, position.getColumn());
        if (getBoard().positionExists(p) && this.canMove(p)){
            mat[p.getRow()][p.getColumn()] = true;
        }
        //Casa Nordeste do Rei
        p.setValues(position.getRow() - 1, position.getColumn() + 1);
        if (getBoard().positionExists(p) && this.canMove(p)){
            mat[p.getRow()][p.getColumn()] = true;
        }
        //Casa Noroeste do Rei
        p.setValues(position.getRow() - 1, position.getColumn() - 1);
        if (getBoard().positionExists(p) && this.canMove(p)){
            mat[p.getRow()][p.getColumn()] = true;
        }
        //Casa Sudeste do Rei
        p.setValues(position.getRow() + 1, position.getColumn() + 1);
        if (getBoard().positionExists(p) && this.canMove(p)){
            mat[p.getRow()][p.getColumn()] = true;
        }
        //Casa Sudoeste do Rei
        p.setValues(position.getRow() + 1, position.getColumn() - 1);
        if (getBoard().positionExists(p) && this.canMove(p)){
            mat[p.getRow()][p.getColumn()] = true;
        }

        return mat;
    }
}
