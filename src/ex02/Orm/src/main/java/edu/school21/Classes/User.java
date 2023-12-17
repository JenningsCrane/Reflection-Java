package edu.school21.Classes;

import edu.school21.Annotations.OrmColumn;
import edu.school21.Annotations.OrmColumnId;
import edu.school21.Annotations.OrmEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@OrmEntity(table = "simple_user")
public class User {
    @OrmColumnId
    private Integer id;
    @OrmColumn(name = "first_name", length = 10)
    private String firstName;
    @OrmColumn(name = "last_name", length = 10)
    private String lastName;
    @OrmColumn(name = "height")
    private Integer height;
    public User() {

    }
}
