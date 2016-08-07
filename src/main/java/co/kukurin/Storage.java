package co.kukurin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Storage {

    private static final Logger logger = LoggerFactory.getLogger(Storage.class);

    public void save(EmailAttachment emailAttachment) {
        logger.info("Saving attachment: {}", emailAttachment);
    }

}
