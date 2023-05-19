package hello.itemservice.config;

import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.jpa.JpaItemRepositoryV2;
import hello.itemservice.repository.jpa.JpaItemRepositoryV3;
import hello.itemservice.repository.jpa.SpringDataJpaItemRepository;
import hello.itemservice.repository.v2.ItemQueryRepositoryV2;
import hello.itemservice.repository.v2.ItemRepositoryV2;
import hello.itemservice.service.ItemService;
import hello.itemservice.service.ItemServiceV1;
import hello.itemservice.service.ItemServiceV2;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;

@Configuration
@RequiredArgsConstructor
public class V2Config {

    private final EntityManager em;
    private final ItemRepositoryV2 itemRepositoryV2;    //SPringDataJPA 가 자동으로 Bean 등록
    @Bean
    public ItemService itemService() {
        return new ItemServiceV2(itemRepositoryV2,itemQueryRepositoryV2());
    }

    @Bean
    public ItemQueryRepositoryV2 itemQueryRepositoryV2(){
        return new ItemQueryRepositoryV2(em);
    }
    @Bean   //Test를 위해서 남겨둔 Repository 등록, 실동작에는 필요하지않다
    public ItemRepository itemRepository() {
        return new JpaItemRepositoryV3(em);
    }

}
