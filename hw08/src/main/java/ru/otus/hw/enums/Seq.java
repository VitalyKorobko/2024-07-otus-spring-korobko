package ru.otus.hw.enums;

public enum Seq {
    COMMENT("commentId"),

    BOOK("bookId");

    private final String nameSeq;

    Seq(String nameSeq) {
        this.nameSeq = nameSeq;

    }

    public String getSeqName() {
        return nameSeq;
    }

}
