package io.endeavour.stocks.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.endeavour.stocks.UnitTestBase;
import io.endeavour.stocks.entity.crud.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;
@AutoConfigureMockMvc
class CrudControllerTest extends UnitTestBase {
    private static final Logger LOGGER = LoggerFactory.getLogger(CrudControllerTest.class);

    //This ObjectMapper is used to convert a JSON String into an Object and vice versa
    ObjectMapper objectMapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();

    ThreadLocal<Person> personThreadLocal = new ThreadLocal<>();

    @Autowired
    MockMvc mockMvc;

    @Test
    public void getPerson_NotFound() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/crud/getPerson/?personID=981");
        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void getPerson_Exists() throws Exception{
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/crud/getPerson/?personID=203");
        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk()) //Checks the Http Response Status Code
                .andReturn(); // We are trying to inspect the Http Response Body contents
        String outputResponse = mvcResult.getResponse().getContentAsString();
        Person outputPerson = objectMapper.readValue(outputResponse, Person.class);
        LOGGER.info("Person object returned from the API is {}", outputPerson);

        assertEquals("Ranbir", outputPerson.getFirstName());
    }
    /**
     * This method can read a file from the given Path and return the contents as a String
     * @param filePath
     * @return Contents of file as String
     * @throws IOException
     */
    static String getJson(String filePath) throws IOException {
        Resource resource = new ClassPathResource(filePath);
        try {
            return Files.readString(resource.getFile().toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Nested
    class UpdateDeletePersonTest{
        @BeforeEach
        public void insertPerson() throws Exception{
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/crud/insertPerson")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(getJson("test-data/create-person.json"));
            LOGGER.info("Json of the Person being inserted is {}",getJson("test-data/create-person.json") );

            MvcResult mvcResult = mockMvc.perform(requestBuilder)
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andReturn();

            String responseString = mvcResult.getResponse().getContentAsString();
            Person insertedPerson = objectMapper.readValue(responseString, Person.class);
            LOGGER.info("Person object after being inserted by the API call is {}", insertedPerson);

            assertTrue(insertedPerson.getPersonID()!=0);
            //assertEquals("godzilla".toUpperCase(), insertedPerson.getLastName().toUpperCase());

            personThreadLocal.set(insertedPerson);
        }

        @Test
        public void updatePerson() throws Exception {
            Person person = personThreadLocal.get();
            LOGGER.info("Person object before the update is {}", person);

            person.setFirstName("Kong");
            person.setLastName("Gigantus");

            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put("/crud/updatePerson/?personID=" + person.getPersonID())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(person));

            MvcResult mvcResult = mockMvc.perform(requestBuilder)
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andReturn();
            String outputResponseString = mvcResult.getResponse().getContentAsString();
            Person updatedPerson = objectMapper.readValue(outputResponseString, Person.class);
            LOGGER.info("Updated person after the API call is {}", updatedPerson);

            assertEquals(person.getPersonID(), updatedPerson.getPersonID());
            assertEquals("Kong".toUpperCase(), updatedPerson.getFirstName().toUpperCase());

        }

        @Test
        public void deletePerson() throws Exception {
            Person person = personThreadLocal.get();
            LOGGER.info("Person object to be deleted is {}", person);
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/crud/deletePerson/" + person.getPersonID());
            mockMvc.perform(requestBuilder)
                    .andExpect(MockMvcResultMatchers.status().isOk());

        }

    }
}