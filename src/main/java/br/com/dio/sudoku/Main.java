package br.com.dio.sudoku;

import br.com.dio.sudoku.model.Board;
import br.com.dio.sudoku.model.Space;

import java.util.*;
import java.util.stream.Stream;

import static br.com.dio.sudoku.util.BoardTemplate.BOARD_TEMPLATE;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toMap;

public class Main {

    private static Board board;
    private final static int BOARD_LIMIT = 9;

    private final static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        final var positions = Stream.of(args)
                .collect(toMap(
                        k -> k.split(";")[0],
                        v -> v.split(";")[1]
                ));

        var option = -1;

        while (true) {
            System.out.println("Selecione uma das opcoes a seguir");
            System.out.println("1 - Iniciar Jogo");
            System.out.println("2 - Colocar um novo numero");
            System.out.println("3 - Remover um numero");
            System.out.println("4 - Visualizar jogo atual");
            System.out.println("5 - Verificar status do jogo");
            System.out.println("6 - Limpar jogo");
            System.out.println("7 - Finalizar jogo");
            System.out.println("8 - Sair");

            option = scanner.nextInt();

            switch (option) {
                case 1 -> startGame(positions);
                case 2 -> inputNumber();
                case 3 -> removeNumber();
                case 4 -> showCurrentGame();
                case 5 -> showGameStatus();
                case 6 -> clearGame();
                case 7 -> finishGame();
                case 8 -> System.exit(0);
                default -> System.out.println("Opcao invalida, selecione uma das opcoes do menu");
            }
        }
    }

    private static void startGame(Map<String, String> positions) {
        if (nonNull(board)) {
            System.out.println("Jogo ja foi iniciado!");
            return;
        }

        List<List<Space>> spaces = new ArrayList<>();

        for (int i = 0; i < BOARD_LIMIT; i++) {
            spaces.add(new ArrayList<>());
            for (int j = 0; j < BOARD_LIMIT; j++) {
                var positionConfig = positions.get("%s,%s".formatted(i, j));
                var expected = Integer.parseInt(positionConfig.split(",")[0]);
                var fixed = Boolean.parseBoolean(positionConfig.split(",")[1]);
                var currentSpace = new Space(expected, fixed);
                spaces.get(i).add(currentSpace);
            }
        }

        board = new Board(spaces);
        System.out.println("Jogo iniciado!");
    }

    private static void inputNumber() {
        if (isNull(board)) {
            System.out.println("O Jogo ainda nao foi iniciado!");
            return;
        }

        System.out.println("Informe a coluna em que o numero sera inserido:");
        var col = runUntilGetValidNumber(0, BOARD_LIMIT);
        System.out.println("Informe a linha em que o numero sera inserido:");
        var row = runUntilGetValidNumber(0, BOARD_LIMIT);
        System.out.printf("Informe o numero que ira para esta posicao: coluna: %d, row: %d\n", col, row);
        var value = runUntilGetValidNumber(1, 9);

        if (!board.changeValue(col, row, value)) {
            System.out.printf("A posicao da coluna: %s e linha: %s, tem um valor fixo!\n", col, row);
        }
    }

    private static void removeNumber() {
        if (isNull(board)) {
            System.out.println("O Jogo ainda nao foi iniciado!");
            return;
        }

        System.out.println("Informe a coluna em que o numero sera removido:");
        var col = runUntilGetValidNumber(0, BOARD_LIMIT);
        System.out.println("Informe a linha em que o numero sera removido:");
        var row = runUntilGetValidNumber(0, BOARD_LIMIT);

        if (!board.clearValue(col, row)) {
            System.out.printf("A posicao da coluna: %s e linha: %s, tem um valor fixo!\n", col, row);
        }
    }

    private static void showCurrentGame() {
        if (isNull(board)) {
            System.out.println("O Jogo ainda nao foi iniciado!");
            return;
        }

        var args = new Object[81];
        var argsPos = 0;

        for (int i = 0; i < BOARD_LIMIT; i++) {
            for (var col : board.spaces()) {
                args[argsPos ++] = " " + ((isNull(col.get(i).getActual())) ? " " : col.get(i).getActual());
            }
        }

        System.out.println("O jogo atual se encontra desta maneira:");
        System.out.printf((BOARD_TEMPLATE) + "\n", args);
    }

    private static void showGameStatus() {
        if (isNull(board)) {
            System.out.println("O Jogo ainda nao foi iniciado!");
            return;
        }

        System.out.printf("O jogo atualmente esta: %s\n", board.getGameStatus().getLabel());

        if (board.hasError()) {
            System.out.println("O jogo contem erros");
        } else {
            System.out.println("O jogo esta correto!");
        }
    }

    private static void clearGame() {
        if (isNull(board)) {
            System.out.println("O Jogo ainda nao foi iniciado!");
            return;
        }

        System.out.println("Deseja realmente limpar seu jogo e perder seu progresso?");

        var confirm = scanner.next();

        while (!confirm.equalsIgnoreCase("sim") && !confirm.equalsIgnoreCase("nao")) {
            System.out.println("Opcao invalida, digite \"sim\" ou \"nao\"");
            confirm = scanner.nextLine();
        }

        if ("sim".equals(confirm)) board.reset();
    }

    private static void finishGame() {
        if (isNull(board)) {
            System.out.println("O Jogo ainda nao foi iniciado!");
            return;
        }

        if (board.gameIsFinished()) {
            System.out.println("Parabens, voce concluiu o jogo");
            showCurrentGame();
            board.reset();
            board = null;
        } else if (board.hasError()) {
            System.out.println("Seu jogo contem erros, verifique seu quadro e ajuste-o.");
        } else {
            System.out.println("O jogo ainda possui espacos vazios");
        }
    }

    private static int runUntilGetValidNumber(final int min, final int max) {
        int current;

        while (true) {
            System.out.printf("Digite um numero entre %d e %d: ", min, max);
            try {
                current = scanner.nextInt();

                if (current >= min && current <= max) {
                    return current;
                } else {
                    System.out.println("Numero fora do intervalo.");
                }

            } catch (InputMismatchException ex) {
                System.out.println("Entrada invalida. Digite apenas numeros inteiros.");
                scanner.nextLine();
            }
        }
    }
}
