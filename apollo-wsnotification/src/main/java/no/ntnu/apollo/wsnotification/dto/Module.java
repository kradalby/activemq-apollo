package no.ntnu.apollo.wsnotification.dto;

import org.apache.activemq.apollo.util.DtoModule;

/**
 * Created by Aleksander Skraastad (myth) on 2/24/15.
 * <p/>
 * apollo-project is licenced under the MIT licence.
 */
public class Module implements DtoModule {
    @Override
    public String dto_package() {
        return "no.ntnu.apollo.wsnotification.dto";
    }

    @Override
    public Class<?>[] extension_classes() {
        return new Class<?>[]{ WsnDTO.class, WsnConnectionStatusDTO.class };
    }
}
