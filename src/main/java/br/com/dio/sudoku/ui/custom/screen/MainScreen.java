package br.com.dio.sudoku.ui.custom.screen;

import br.com.dio.sudoku.model.Space;
import br.com.dio.sudoku.service.BoardService;
import br.com.dio.sudoku.service.EventEnum;
import br.com.dio.sudoku.service.NotifierService;
import br.com.dio.sudoku.ui.custom.button.CheckGameStatusButton;
import br.com.dio.sudoku.ui.custom.button.FinishGameButton;
import br.com.dio.sudoku.ui.custom.button.ResetButton;
import br.com.dio.sudoku.ui.custom.frame.MainFrame;
import br.com.dio.sudoku.ui.custom.input.NumberText;
import br.com.dio.sudoku.ui.custom.panel.MainPanel;
import br.com.dio.sudoku.ui.custom.panel.SudokuSector;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainScreen {

    private final static Dimension dimension = new Dimension(600, 600);

    private final BoardService boardService;
    private final NotifierService notifierService = new NotifierService();

    private JButton finishGameButton;
    private JButton checkGameStatusButton;
    private JButton resetButton;

    public MainScreen(final Map<String, String> gameConfig) {
        this.boardService = new BoardService(gameConfig);
    }

    public void init() {
        JPanel mainPanel = new MainPanel(dimension);
        JFrame mainFrame = new MainFrame(dimension, mainPanel);

        for (int r = 0; r < 9; r += 3) {
            var endRow = r + 2;
            for (int c = 0; c < 9; c += 3) {
                var endCol = c + 2;
                var spaces = getSpacesFromSector(boardService.getSpaces(), c, endCol, r, endRow);
                mainPanel.add(generateSection(spaces));
            }
        }

        addResetButton(mainPanel);
        addCheckGameStatusButton(mainPanel);
        addFinishGameButton(mainPanel);

        mainFrame.revalidate();
        mainFrame.repaint();
    }

    private List<Space> getSpacesFromSector(
            final List<List<Space>> spaces,
            int initCol,
            int endCol,
            int initRow,
            int endRow
    ) {
        List<Space> spaceSector = new ArrayList<>();
        for (int r = initRow; r <= endRow; r ++) {
            for (int c = initCol; c <= endCol; c ++) {
                spaceSector.add(spaces.get(c).get(r));
            }
        }

        return spaceSector;
    }

    private JPanel generateSection(final List<Space> spaces) {
        List<NumberText> fields = new ArrayList<>(spaces.stream().map(NumberText::new).toList());
        fields.forEach(field -> notifierService.subscriber(EventEnum.CLEAR_SPACE, field));
        return new SudokuSector(fields);
    }

    private void addFinishGameButton(JPanel mainPanel) {
        finishGameButton = new FinishGameButton(e -> {
            if (boardService.gameIsFinished()) {
                JOptionPane.showMessageDialog(null, "Parabens, voce concluiu o jogo!");
                resetButton.setEnabled(false);
                checkGameStatusButton.setEnabled(false);
                finishGameButton.setEnabled(false);
            } else {
                JOptionPane.showMessageDialog(null, "Seu jogo contem algo de errado, ajuste e tente novamente!");
            }
        });

        mainPanel.add(finishGameButton);
    }

    private void addCheckGameStatusButton(JPanel mainPanel) {
        checkGameStatusButton = new CheckGameStatusButton(e -> {
            var hasErrors = boardService.hasErrors();
            var gameStatus = boardService.getStatus();
            var message = switch (gameStatus) {
                case NON_INITIALIZED -> "O jogo nao foi iniciado";
                case INCOMPLETE -> "O jogo esta incompleto";
                case COMPLETED -> "O jogo esta completo";
            };

            message += hasErrors ? " e contem erros" : " e nao contem erros";
            JOptionPane.showMessageDialog(null, message);
        });

        mainPanel.add(checkGameStatusButton);
    }

    private void addResetButton(JPanel mainPanel) {
        resetButton = new ResetButton(e -> {
            var dialogResult = JOptionPane.showConfirmDialog(
                    null,
                    "Deseja realmente reiniciar o jogo?",
                    "Limpar o Jogo",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (dialogResult == JOptionPane.YES_OPTION) {
                boardService.reset();
                notifierService.notify(EventEnum.CLEAR_SPACE);
            }
        });
        mainPanel.add(resetButton);
    }
}
