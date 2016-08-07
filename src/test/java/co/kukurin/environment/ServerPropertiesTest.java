package co.kukurin.environment;

import co.kukurin.server.environment.ServerProperties;
import org.junit.Test;

import java.util.Properties;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class ServerPropertiesTest {

    @Test
    public void shouldLoadIntegerProperties() throws Exception {
        // given
        String givenKey = "givenKey";
        Integer givenDefaultValue = 123;
        String givenPropertyValueAsString = "123";

        Properties properties = spy(Properties.class);
        when(properties.getOrDefault(givenKey, givenDefaultValue))
            .thenReturn(givenPropertyValueAsString);

        // when
        Integer whenInteger = new ServerProperties(properties).getOrDefaultInt(givenKey, givenDefaultValue);

        // then
        then(whenInteger).isEqualTo(Integer.parseInt(givenPropertyValueAsString));
    }
}
