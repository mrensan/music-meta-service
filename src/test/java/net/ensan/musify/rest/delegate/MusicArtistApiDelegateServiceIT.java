package net.ensan.musify.rest.delegate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.configuration.Orthography;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import net.ensan.musify.api.model.MusicArtistDetailsAlbumsDto;
import net.ensan.musify.api.model.MusicArtistDetailsDto;
import net.ensan.musify.util.AbstractBaseIntegrationTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@DBRider
@DBUnit(mergeDataSets = true,
    cacheConnection = false,
    allowEmptyFields = true,
    caseInsensitiveStrategy = Orthography.LOWERCASE)
@DataSet(value = "MusicArtistApiDelegateServiceIT/initial.yml",
    cleanBefore = true,
    cleanAfter = true,
    useSequenceFiltering = false,
    disableConstraints = true)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class MusicArtistApiDelegateServiceIT extends AbstractBaseIntegrationTest {

    public static final String MUSIFY_ENDPOINT_URL = "/musify/music-artist/details/%s";
    public static final String MBID = "1f9df192-a621-4f54-8850-2c5373b7eac9";
    private static final WireMockServer WIRE_MOCK_SERVER;

    static {
        WIRE_MOCK_SERVER = new WireMockServer(
            WireMockConfiguration.options()
                .usingFilesUnderClasspath("wiremock")
                .extensions(new ResponseTemplateTransformer(true))
                .dynamicPort()
        );
    }

    private static WireMockServer getWiremockServer() {
        return Optional.ofNullable(WIRE_MOCK_SERVER)
            .filter(WireMockServer::isRunning)
            .orElseThrow(() -> new IllegalStateException("WireMock server is not running! "
                + "Verify if WiremockTestExtension was included as an extension to your test class."));
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll() {
        WIRE_MOCK_SERVER.start();
    }

    @AfterAll
    static void afterAll() {
        WIRE_MOCK_SERVER.stop();
    }

    @DynamicPropertySource
    static void databaseProperties(final DynamicPropertyRegistry registry) {
        registry.add("wiremock-port", getWiremockServer()::port);
    }

    @Test
    void musicArtistDetailsShouldReturn200AndMusicArtistDetailsDto() throws Exception {
        final String musifyEndpoint = String.format(MUSIFY_ENDPOINT_URL, MBID);

        final MvcResult mvcResult = mockMvc.perform(get(musifyEndpoint)
                .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();
        final String responseAsJson = mvcResult.getResponse().getContentAsString();
        final MusicArtistDetailsDto dto =
            objectMapper.readValue(responseAsJson, MusicArtistDetailsDto.class);
        assertThat(dto)
            .isNotNull()
            .extracting(
                MusicArtistDetailsDto::getMbid,
                MusicArtistDetailsDto::getName,
                MusicArtistDetailsDto::getCountry,
                MusicArtistDetailsDto::getGender,
                MusicArtistDetailsDto::getDisambiguation,
                MusicArtistDetailsDto::getDescription
            ).containsExactly(
                "1f9df192-a621-4f54-8850-2c5373b7eac9",
                "Ludwig van Beethoven",
                "DE",
                "male",
                "German composer",
                "was a German composer and pianist."
            );
        assertThat(dto.getAlbums())
            .isNotNull()
            .hasSize(2)
            .extracting(
                MusicArtistDetailsAlbumsDto::getId,
                MusicArtistDetailsAlbumsDto::getTitle,
                MusicArtistDetailsAlbumsDto::getImageUrl
            ).containsExactly(
                tuple(
                    "0db33ecd-ddef-4721-8877-7a4b2df993ca",
                    "Symphonies 1 & 8",
                    "http://coverartarchive.org/release/08903b2b-742d-4c9e-a5e8-048d29e1d690/15390502503.jpg"
                ),
                tuple(
                    "11d498ee-7fc2-4b8c-986d-578d4db8bf6a",
                    "Beethoven: Symphony No. 7",
                    "http://coverartarchive.org/release/7a67cd80-f043-46af-be01-b708e50455d0/27883567645.jpg"
                )
            );
    }
}
