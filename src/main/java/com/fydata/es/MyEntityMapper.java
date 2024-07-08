package com.fydata.es;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import org.springframework.data.elasticsearch.core.EntityMapper;
import org.springframework.data.elasticsearch.core.geo.CustomGeoModule;
import org.springframework.data.elasticsearch.core.mapping.ElasticsearchPersistentEntity;
import org.springframework.data.elasticsearch.core.mapping.ElasticsearchPersistentProperty;
import org.springframework.data.mapping.MappingException;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import java.io.IOException;
import java.util.*;

@Component
public class MyEntityMapper implements EntityMapper {

    private ObjectMapper objectMapper;

    public MyEntityMapper(MappingContext<? extends ElasticsearchPersistentEntity<?>, ElasticsearchPersistentProperty> context) {
        Assert.notNull(context, "MappingContext must not be null!");
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new MyEntityMapper.SpringDataElasticsearchModule(context));
        this.objectMapper.registerModule(new CustomGeoModule());
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
    }

    public String mapToString(Object object) throws IOException {
        return this.objectMapper.writeValueAsString(object);
    }

    public Map<String, Object> mapObject(Object source) {
        try {
            return (Map)this.objectMapper.readValue(this.mapToString(source), HashMap.class);
        } catch (IOException var3) {
            throw new MappingException(var3.getMessage(), var3);
        }
    }

    public <T> T mapToObject(String source, Class<T> clazz) throws IOException {
        return this.objectMapper.readValue(source, clazz);
    }

    public <T> T readObject(Map<String, Object> source, Class<T> targetType) {
        try {
            return this.mapToObject(this.mapToString(source), targetType);
        } catch (IOException var4) {
            throw new MappingException(var4.getMessage(), var4);
        }
    }

    private static class SpringDataElasticsearchModule extends SimpleModule {
        private static final long serialVersionUID = -9168968092458058966L;

        public SpringDataElasticsearchModule(MappingContext<? extends ElasticsearchPersistentEntity<?>, ElasticsearchPersistentProperty> context) {
            Assert.notNull(context, "MappingContext must not be null!");
            this.setSerializerModifier(new MyEntityMapper.SpringDataElasticsearchModule.SpringDataSerializerModifier(context));
        }

        private static class SpringDataSerializerModifier extends BeanSerializerModifier {
            private final MappingContext<? extends ElasticsearchPersistentEntity<?>, ElasticsearchPersistentProperty> context;

            public SpringDataSerializerModifier(MappingContext<? extends ElasticsearchPersistentEntity<?>, ElasticsearchPersistentProperty> context) {
                Assert.notNull(context, "MappingContext must not be null!");
                this.context = context;
            }

            public List<BeanPropertyWriter> changeProperties(SerializationConfig config, BeanDescription description, List<BeanPropertyWriter> properties) {
                Class<?> type = description.getBeanClass();
                ElasticsearchPersistentEntity<?> entity = this.context.getPersistentEntity(type);
                if (entity == null) {
                    return super.changeProperties(config, description, properties);
                } else {
                    List<BeanPropertyWriter> result = new ArrayList(properties.size());
                    Iterator var7 = properties.iterator();

                    while(var7.hasNext()) {
                        BeanPropertyWriter beanPropertyWriter = (BeanPropertyWriter)var7.next();
                        ElasticsearchPersistentProperty property = entity.getPersistentProperty(beanPropertyWriter.getName());
                        if (property != null && property.isWritable()) {
                            result.add(beanPropertyWriter);
                        }
                    }

                    return result;
                }
            }
        }
    }
}
