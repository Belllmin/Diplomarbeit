package com.htlleonding.ac.at.backend.entity;

import com.sun.istack.NotNull;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Entity
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(	name = "user",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "username"),
                @UniqueConstraint(columnNames = "email")
        })
@ApiModel(description = "Details about user.")
public class User {

    //region Field(s)
    @Id
    //@GeneratedValue(strategy = GenerationType.AUTO) //Für long oder int, aber nicht string
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @ApiModelProperty(notes = "The unique id of the user.")
    private String id;

    @ApiModelProperty(notes = "The status of the user.")
    private boolean isBlocked = false;

    @ApiModelProperty(notes = "Is the user deactivated?.")
    private boolean isActivated = true;

    /*
     * The field verificationCode stores a random, unique String which
     * is generated in the registration process and will be used in the
     * verification process. Once registered, the enabled status of a
     * user is false (disabled) so the user can’t login if he has not
     * activated account by checking email and click on the verification
     * link embedded in the email.
    */
    @Column(name = "verification_code", length = 64)
    private String verificationCode;

    private boolean enabled = false;

    @NotNull
    @NotBlank
    @Size(max = 20)
    @ApiModelProperty(notes = "The user's name.")
    private String userName;

    @ApiModelProperty(notes = "Visibility of user's phone number.")
    private boolean showPhoneNumber;

    @ApiModelProperty(notes = "The user's profile picture.")
    private byte[] image;

    @NotBlank
    @Size(max = 200)
    @ApiModelProperty(notes = "User's status.") // z.B.: wie Whatsapp status
    private String bio;

    @NotBlank
    @Size(max = 50)
    @Email
    @ApiModelProperty(notes = "The user's email.")
    private String email;

    @NotBlank
    @Size(max = 120)
    @ApiModelProperty(notes = "The user's password.")
    private String password;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @OneToMany(targetEntity = Product.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_fk", referencedColumnName = "id")
    private List<Product> products = new ArrayList<>();
    //endregion

    //region Constructors
    public User(String userName, String email, String password) {
        this.userName = userName;
        this.email = email;
        this.password = password;
    }
    //endregion
}