package hello.itemservice.domain;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity //JPA에서 관리하는 객체 (JPA가 인식가능)
//@Table(name = "Item") 객체명과 테이블명이 같을경우 생략가능
public class Item {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;    //P.K 라는것을 명시

    @Column(name = "item_name", length = 10)    //Column 명시
    //Column(name 설정할때 camleCase <-> snake_case 간 변환을 알잘딱해줘서 사실생략가능
    private String itemName;
    private Integer price;
    private Integer quantity;

    public Item() {
    }

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}
