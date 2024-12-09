package com.ezen.books.domain;

import lombok.*;

@NoArgsConstructor
@ToString
@Setter
@Getter
public class DataUrlDTO {

    private String pctg;
    private String sctg;
    private String url;

    public DataUrlDTO(String pctg, String sctg, String url){
        this.pctg = pctg;
        this.sctg = sctg;
        this.url = url;
    }

}
