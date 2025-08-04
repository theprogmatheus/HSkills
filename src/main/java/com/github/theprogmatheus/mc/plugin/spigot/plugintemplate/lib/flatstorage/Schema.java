package com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.lib.flatstorage;

import lombok.Getter;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.zip.CRC32C;

@Getter
public class Schema<T> {

    private static final CRC32C CRC32C = new CRC32C();

    private final Class<T> typeClass;
    private final List<FieldSchema> fields;
    private final long schemaVersion;
    private final int recordSize;

    public Schema(Class<T> typeClass) {
        this.typeClass = typeClass;
        this.fields = mapFields();
        this.schemaVersion = generateSchemaVersion();
        this.recordSize = calculateRecordSize();
    }

    private List<FieldSchema> mapFields() {
        List<FieldSchema> fieldSchemas = new ArrayList<>();
        Field[] fields = this.typeClass.getDeclaredFields();
        for (Field field : fields) {
            int mod = field.getModifiers();
            if (Modifier.isStatic(mod) || Modifier.isTransient(mod)
                    || Modifier.isFinal(mod) || Modifier.isStrict(mod))
                continue;

            String name = field.getName();
            Class<?> type = field.getType();
            int recordSize = getRecordSizeByFieldType(type);
            boolean pointer = recordSize == -1;
            fieldSchemas.add(new FieldSchema(name, type, pointer ? 8 : recordSize, pointer));
        }
        fieldSchemas.sort(Comparator.comparing(FieldSchema::getName)); // para manter consistência
        return fieldSchemas;
    }

    private int getRecordSizeByFieldType(Class<?> type) {
        if (Byte.class.isAssignableFrom(type) || byte.class.isAssignableFrom(type))
            return 1;
        if (Short.class.isAssignableFrom(type) || short.class.isAssignableFrom(type))
            return 2;
        if (Integer.class.isAssignableFrom(type) || int.class.isAssignableFrom(type))
            return 4;
        if (Float.class.isAssignableFrom(type) || float.class.isAssignableFrom(type))
            return 4;
        if (Character.class.isAssignableFrom(type) || char.class.isAssignableFrom(type))
            return 2;
        if (Boolean.class.isAssignableFrom(type) || boolean.class.isAssignableFrom(type))
            return 1;
        if (Double.class.isAssignableFrom(type) || double.class.isAssignableFrom(type))
            return 8;
        if (Long.class.isAssignableFrom(type) || long.class.isAssignableFrom(type))
            return 8;
        if (UUID.class.isAssignableFrom(type))
            return 16;
        return -1;
    }

    private long generateSchemaVersion() {
        CRC32C.reset();
        StringBuilder stringBuilder = new StringBuilder();
        for (FieldSchema field : this.fields) {
            stringBuilder.append(field.getName());
            stringBuilder.append(field.getType().getName());
        }
        byte[] schemaBytes = stringBuilder.toString().getBytes(StandardCharsets.UTF_8);
        CRC32C.update(schemaBytes);
        return CRC32C.getValue();
    }

    private int calculateRecordSize() {
        int size = this.fields.stream()
                .map(FieldSchema::getRecordSize)
                .reduce(0, Integer::sum);

        size += 8; // reservado para a versão do schema
        return size;
    }
}
