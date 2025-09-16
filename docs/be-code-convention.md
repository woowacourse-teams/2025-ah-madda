## 1. 공통 규칙

### 1.1 IntelliJ Code Style은 [우테코 스타일](https://github.com/woowacourse/woowacourse-docs/blob/main/styleguide/java/intellij-java-wooteco-style.xml)을 적용한다.

## 2. 선언 규칙
### 2.1 클래스 상수, 멤버 변수, Static 메서드, Instatnce 메서드순으로 선언한다.
```java
public class Example {

    private static final String SYSTEM_NAME = "Example";

    private static int count;

    private String name;

    public static int getCount() {
        return count;
    }

    public String getName() {
        return name;
    }
}
```

### 2.2 final은 기본적으로 선언하지 않는다. 필요한 경우에만 선언한다.
```java
public class Example {

    private final String id;
    private final String name;

    public Example(final String id, final String name) {
        this.id = id;
        this.name = name;
    }
}
```

### 2.3 entity 클래스에는 equals, hashcode, toString을 기본적으로 선언하지 않는다. 필요한 경우에만 선언한다. 
```java
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    private String name;

    // 필요 시 명확한 이유를 설명하고 equals, hashCode 작성
}
```

### 2.4 모든 private 메서드는 public 메서드 하단에 위치시킨다.
```java
public class Member {

    private String name;
    private int age;

    public void updateName(String newName) {
        validateName(newName);
        this.name = newName;
    }

    public void celebrateBirthday() {
        if (!isAdult()) {
            throw new IllegalStateException("미성년자는 생일 축하 대상이 아닙니다.");
        }
        this.age++;
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("이름은 필수입니다.");
        }
    }

    private boolean isAdult() {
        return this.age >= 20;
    }
}
```

### 2.5 예외적으로, getter, equals, hashCode, toString 등 Java 객체의 기본 메서드는 private 메서드보다 하단에 위치시킨다.
```java
public class Member {

    public void updateName(String newName) {
        validateName(newName);
        this.name = newName;
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("이름은 필수입니다.");
        }
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        ...
    }

    @Override
    public int hashCode() {
        ...
    }

    @Override
    public String toString() {
        ...
    }
}
```

## 3. 개행 규칙
### 3.1 클래스, 인터페이스, 레코드 선언 아래에 개행을 추가한다.
```java
@Getter
public class Example {

    private String name;
    private int age;
}
```

### 3.2 어노테이션이 붙은 필드가 있는 경우, 필드 사이에 개행을 추가한다.
```java
@Entity
@Getter
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Name name;

    @Embedded
    private Email email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
}
```

### 3.3 메서드 시그니처의 길이가 120줄이 넘어갈 경우, 파라미터 사이에 개행을 추가한다.
```java
public Example createExample(
        String arg1,
        String arg2,
        String arg3,
        String arg4,
        String arg5
) {
    return new Example();
}
```

### 3.4 Interface의 메서드 사이에 개행을 추가한다.
```java
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    
    Optional<Reservation> findReservationByDateAndTimeAndTheme(LocalDate date, ReservationTime time, Theme theme);

    boolean existsByTimeId(long timeId);

    boolean existsByThemeId(long themeId);
}
```

### 3.5 인자가 여러 줄에 걸쳐 있을 때, 마지막 인자에 개행을 추가한다.
```java
Command command = new Command(
    arg1,
    arg2
);
```

### 3.6 메서드 내부의 의미가 바뀌는 지점에 개행을 추가한다.
```java
void method() {
    User user = createUser();

    user.activate();

    return user;
}
```

### 3.7 stream은 점(.) 마다 개행을 추가한다.
```java
public List<ProductResponse> findAvailableProducts() {
    return productRepository.findAll()
            .stream()
            .filter(Product::isAvailable)
            .map(ProductResponse::from)
            .sorted(Comparator.comparing(ProductRespons
```  

## 4. 주석 규칙

### 4.1 함수 주석이 필요하다면, 사유-param-return 순으로 작성한다.
```java
/**
 * 상품을 조회한다.
 *
 * @param id 조회할 상품의 ID
 * @return 조회된 상품
 */
public Product findProduct(Long id) {
    ...
}
```

### 4.2 한 줄 주석이 필요하다면, 코드 위에 작성한다.
```java
// 이름 검증
if (name == null || name.isBlank()) {
    throw new IllegalArgumentException("이름은 필수입니다.");
}
```

## 5. Test 코드 규칙

### 5.1 Test 코드는 given, when, then 양식으로 작성한다.
```java
@Test
void 회원_생성_성공() {
    // given
    Member member = new Member("name");

    // when
    memberRepository.save(member);

    // then
    assertThat(member.getId()).isNotNull();
}
```

### 5.2 when과 then이 같이 있는 경우 `// when // then` 으로 주석을 작성한다. 
```java
@Test
void 없는_회원_조회시_예외가_발생한다() {
    // when // then
    assertThatThrownBy(() -> memberService.findById(-1L))
            .isInstanceOf(MemberNotFoundException.class);
}
```

### 5.3 @DisplayName을 쓰지 않고 메서드명을 한글로 작성한다.
```java
@Test
void 회원_정보를_수정한다() {
    ...
}
```

### 5.4 메서드 이름은 명령형 어조로 작성한다.
```java
@Test
void 회원을_삭제한다() {
    ...
}
```

### 5.5 예외를 검증하는 테스트는 `~_예외가_발생한다` 형식으로 메서드 이름을 작성한다.
```java
@Test
void 존재하지_않는_상품이면_예외가_발생한다() {
    ...
}
```

### 5.6 여러번의 검증이 필요할 경우, assertSoftly를 사용한다.
```java
@Test
void 여러_필드를_검증한다() {
    assertSoftly(softly -> {
        softly.assertThat("abc").startsWith("a");
        softly.assertThat(10).isGreaterThan(5);
    });
}
```

### 5.7 "의미 있는 동작 단위"를 검증하기 위해 통합 테스트가 필요한 경우, @SpringBootTest를 사용한다.
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class MemberServiceTest {
    ...
}
```

### 5.8 테스트 수행 후 롤백이 필요한 경우, @Transactional을 사용한다.
```java
@Transactional
class MemberServiceTest {
    ...
}
```

### 5.9 PR을 올리기 전에 IntelliJ의 커버리지 테스트로 "의미 있는 동작 단위"가 모두 테스트되고 있는지 확인한다.
<img width="737" height="81" alt="image" src="https://github.com/user-attachments/assets/e9535b30-8dfa-4edb-8d61-99bffd7b0191" />


### 5.10 테스트 픽스처가 필요한 경우, 가능한 한 헬퍼 메서드를 활용한다.
```java
private Order createTestOrder(String customerId, double amount) {
        return new Order(customerId, amount);
}
```

### 5.11 테스트 대상 객체는 `sut(System Under Test)`이라는 이름을 사용한다.
```java
class CalculatorTest {

    private Calculator sut;

    @Test
    void 두_정수를_더한다() {
        var actualSum = sut.add(1, 2);
    }
}
```

### 5.12 지역 변수 선언에는 `var`를 사용한다.
```java
@Test
void 두_정수를_더한다() {
    var actualSum = sut.add(1, 2);
}
```

### 5.13 테스트에서는 static import를 사용한다.
```java

@Test
void 여러_필드를_검증한다() {
    assertSoftly(softly -> {
        softly.assertThat("abc").startsWith("a");
        softly.assertThat(10).isGreaterThan(5);
    });
}
```

## 6. DTO 규칙

### 6.1 DTO는 record로 생성한다.
```java
public record ProductResponse(
        long productId,
        String name,
        int price
) {
}
```

### 6.2 DTO 클래스 이름은 XXXRequest, XXXResponse 형식으로 작성한다.
```java
public record ProductCreateRequest(
        @NotBlank String name,
        @NotNull Integer price
) {
}

```

## 7. 데이터베이스 규칙

### 7.1 테이블명은 집합명사(collective noun)를 사용하며, 단수형을 사용한다.

예시) member, reservation

### 7.2 컬럼명은 단수형을 사용하며, 소문자를 사용한다.
```sql
CREATE TABLE member (
    name VARCHAR(255),
    date DATE
);
```

7.3 기본 키 이름을 단순히 id로 사용하지 않는다.
```sql
CREATE TABLE reservation (
    reservation_id BIGINT PRIMARY KEY,
    member_id BIGINT
);
```