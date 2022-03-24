package org.springframework.samples.petclinic.graphql;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureHttpGraphQlTester;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.graphql.test.tester.WebGraphQlTester;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureHttpGraphQlTester
public class VetMutationTest {

    @Autowired
    private WebGraphQlTester graphQlTester;

    /**
     * EXAMPLE:
     * --------------------------
     * <p>
     * Mutation returning a union type (AddVetPayload) with data or error, returns data if invoked correctly
     */
    @Test
    public void addingVetUsingManagerRoleWorks() {
        String query = "mutation newVet {" +
            "  addVet(input:{" +
            "    firstName:\"Peter\"," +
            "    lastName:\"Heinz\"," +
            "    specialtyIds:[1]" +
            "  }) {" +
            "    ...on AddVetSuccessPayload {" +
            "      vet {" +
            "        id" +
            "        specialties {" +
            "          id" +
            "        }" +
            "      }" +
            "    }" +
            "    ...on AddVetErrorPayload {" +
            "      error" +
            "    }" +
            "  }" +
            "}";

        this.graphQlTester.mutate()
            .headers(TestTokens::withManagerToken)
            .build()
            .document(query)
            .execute()
            .path("addVet.vet.id").hasValue()
            .path("addVet.vet.specialties[0].id").entity(String.class).isEqualTo("1");
    }

    /**
     * EXAMPLE:
     * --------------------------
     * <p>
     * Mutation returning a union type (AddVetPayload) with data or error, returns domain error object
     */
    @Test
    public void addingVetWithUnknownSpecialtyFails() {
        String query = "mutation newVet {" +
            "  addVet(input:{" +
            "    firstName:\"Klaus\"," +
            "    lastName:\"Smith\"," +
            "    specialtyIds:[666]" +
            "  }) {" +
            "    ...on AddVetSuccessPayload {" +
            "      vet {" +
            "        id" +
            "        specialties {" +
            "          id" +
            "        }" +
            "      }" +
            "    }" +
            "    ...on AddVetErrorPayload {" +
            "      error" +
            "    }" +
            "  }" +
            "}";

        this.graphQlTester.
            mutate()
            .headers(TestTokens::withManagerToken)
            .build()
            .document(query)
            .execute()
            .path("addVet.vet").pathDoesNotExist()
            .path("addVet.error").entity(String.class).isEqualTo("Specialty with Id '666' not found");
    }

    /**
     * EXAMPLE:
     * --------------------------
     * <p>
     * Mutation is secured using fine-grained security with @PreAuth
     */
    @Test
    public void addingVetWithUserRoleIsNotAllowed() {
        String query = "mutation newVet {" +
            "  addVet(input:{" +
            "    firstName:\"Klaus\"," +
            "    lastName:\"Smith\"," +
            "    specialtyIds:[666]" +
            "  }) {" +
            "    ...on AddVetSuccessPayload {" +
            "      vet {" +
            "        id" +
            "        specialties {" +
            "          id" +
            "        }" +
            "      }" +
            "    }" +
            "    ...on AddVetErrorPayload {" +
            "      error" +
            "    }" +
            "  }" +
            "}";

        this.graphQlTester.
            mutate()
            .headers(TestTokens::withUserToken)
            .build()
            .document(query)
            .execute()
            .errors()
            .satisfy(errors -> {
                assertThat(errors).hasSize(1);
                assertThat(errors.get(0).getErrorType()).isEqualTo(ErrorType.FORBIDDEN);
            })
            .path("addVet.vet").pathDoesNotExist()
            .path("addVet.error").pathDoesNotExist();
    }

}
