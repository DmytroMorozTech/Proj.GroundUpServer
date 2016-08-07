package co.kukurin.environment;

import co.kukurin.server.environment.ServerProperties;
import org.junit.Test;

import java.util.Properties;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class ServerPropertiesTest {

    @Test
    public void shouldLoadIntegerPropertiesByKey() throws Exception {
        // given
        String givenKey = "givenKey";
        String givenPropertyValueAsString = "123";

        Properties properties = mock(Properties.class);
        when(properties.get(eq(givenKey)))
            .thenReturn(givenPropertyValueAsString);

        // when
        Integer whenAnyDefaultValue = 555;
        Integer whenInteger = new ServerProperties(properties).getOrDefaultInt(givenKey, whenAnyDefaultValue);

        // then
        then(whenInteger).isEqualTo(Integer.parseInt(givenPropertyValueAsString));
    }

    @Test
    public void shouldLoadDefaultIntProperties() throws Exception {
        // given
        Integer givenDefaultValue = 123;
        String givenPropertyValueAsString = "123";

        Properties properties = mock(Properties.class);
        when(properties.getOrDefault(any(), eq(givenDefaultValue)))
                .thenReturn(givenDefaultValue);

        // when
        Integer whenInteger = new ServerProperties(properties).getOrDefaultInt("anyKey", givenDefaultValue);

        // then
        then(whenInteger).isEqualTo(Integer.parseInt(givenPropertyValueAsString));
    }
}
