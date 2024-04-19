package application;

import chess.ChessException;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.ChessPosition;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        ChessMatch chessMatch = new ChessMatch();
        List<ChessPiece> captured = new ArrayList<>();

        while (!chessMatch.getCheckMate()){
            try{
                UI.clearScreen();
                UI.printMatch(chessMatch, captured);
                System.out.println();
                System.out.println("Posição de inicio:");
                ChessPosition source = UI.readChessPosition(sc);

                boolean[][] movimentosPossiveis = chessMatch.possibleMoves(source);
                UI.clearScreen();
                UI.printBoard(chessMatch.getPieces(), movimentosPossiveis);

                System.out.println();
                System.out.println("Posição de final");
                ChessPosition target = UI.readChessPosition(sc);

                ChessPiece capturedPiece = chessMatch.performChessMove(source, target);

                if (capturedPiece != null){
                    captured.add(capturedPiece);
                }

                if (chessMatch.getPromoted() != null){
                    System.out.println("Digite a letra da peça (Q/B/N/R) que você deseja: ");
                    String type = sc.nextLine().toUpperCase();
                    while (!type.equals("Q") && !type.equals("B") && !type.equals("N") && !type.equals("R")){
                        System.out.println("VALOR INVALIDO! Digite a letra da peça (Q/B/N/R) que você deseja: ");
                        type = sc.nextLine().toUpperCase();
                    }
                    chessMatch.replacePromotedPiece(type);
                }
            }
            catch (ChessException e){
                System.out.println(e.getMessage());
                sc.nextLine();
            }
            catch (InputMismatchException e){
                System.out.println(e.getMessage());
                sc.nextLine();
            }
            catch (InvalidParameterException e){
                System.out.println(e.getMessage());
                sc.nextLine();
            }
        }
        UI.clearScreen();
        UI.printMatch(chessMatch, captured);

    }
}