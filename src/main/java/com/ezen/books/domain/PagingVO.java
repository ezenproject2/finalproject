package com.ezen.books.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Arrays;
import java.util.List;


@AllArgsConstructor
@ToString
@Setter
@Getter
public class PagingVO {
    private int pageNo;
    private int qty;

    // 검색을 위한 멤버변수
    private String type;
    private String keyword;
    private String order;

    private String primaryCtg;
    private String secondaryCtg;

    // 리뷰를 위한 멤버변수
    private long prno;
    private long mno;

    // 공지사항/FAQ 라인 페이징에 필요한 멤버변수
    private String category;

    // 기본생성자는 커스텀이 필요하므로 @NoArgsConstructor는 붙이지 않았음
    public PagingVO(){
        this.pageNo = 1;
        this.qty = 6;
    }

    public PagingVO(int pageNo, int qty){
        this.pageNo = pageNo;
        this.qty = qty;
    }

    // 리뷰를 위한 생성자
    public PagingVO(int pageNo, int qty, long prno, long mno){
        this.pageNo = pageNo;
        this.qty = qty;
        this.prno = prno;
        this.mno = mno;
    }

    // limit 번지,개수에서 번지를 구해줄 메서드
    public int getStartIndex(){
        return (this.pageNo-1)*qty;
    }

    public List<String> getTypeToArray(){
        if(type == null){
            return null;
        }else{
            List<String> list = Arrays.stream(type.split("")).toList();
            return list;
        }
    }
}