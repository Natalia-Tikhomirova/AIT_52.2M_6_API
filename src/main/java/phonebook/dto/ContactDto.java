package phonebook.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Setter
@Getter
@ToString
@Builder
public class ContactDto {
    private List<ContactDto> contacts;
    private String name;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private String description;
}

