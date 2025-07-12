package br.com.dio.sudoku.model;

public enum GameStatus {
    NON_INITIALIZED("Nao inicializado"),
    INCOMPLETE("Incompleto"),
    COMPLETED("Finalizado");

    private final String label;

    GameStatus(final String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
