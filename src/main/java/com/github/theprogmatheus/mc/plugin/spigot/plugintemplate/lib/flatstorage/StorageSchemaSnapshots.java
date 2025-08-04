package com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.lib.flatstorage;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.*;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
public class StorageSchemaSnapshots extends StorageData {

    private final Map<Long, SchemaSnapshot> snapshots;

    public StorageSchemaSnapshots() {
        this.snapshots = new HashMap<>();
    }

    @Override
    public void write(DataOutput output) throws IOException {
        byte[] payload;
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream)) {
            dataOutputStream.writeInt(this.snapshots.size()); // tamanho do mapa.
            for (SchemaSnapshot snapshot : this.snapshots.values()) {

                dataOutputStream.writeUTF(snapshot.getClassName()); // salvar o nome da classe
                dataOutputStream.writeLong(snapshot.getSchemaVersion()); // salva a versão do schema
                dataOutputStream.writeInt(snapshot.getRecordSize()); // salva o record size

                Map<String, SchemaFieldSnapshot> fields = snapshot.getFields();

                dataOutputStream.writeInt(fields.size()); // salva o tamanho do mapa dos fields
                for (SchemaFieldSnapshot field : fields.values()) {
                    dataOutputStream.writeUTF(field.getClassType()); // salva o tipo do field (acho que nem precisa, considerando o controle de versão)
                    dataOutputStream.writeUTF(field.getName()); // salva o nome do campo
                    dataOutputStream.writeInt(field.getRecordSize()); // salva o tamanho do registro (necessário aqui?, vou manter)
                    dataOutputStream.writeLong(field.getOffset());// salva o ponteiro para o campo.  depois  para encontrar o dado do field é só usar offset + pointer
                }
            }

            payload = byteArrayOutputStream.toByteArray();
        }
        super.setAlive(true);
        super.setPayload(payload);
        super.write(output);
    }

    @Override
    public void read(DataInput input) throws IOException {
        super.read(input);

        Map<Long, SchemaSnapshot> loaded = new HashMap<>();
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(getPayload());
             DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream)) {

            int size = dataInputStream.readInt();
            for (int i = 0; i < size; i++) {
                String className = dataInputStream.readUTF();
                long schemaVersion = dataInputStream.readLong();
                int recordSize = dataInputStream.readInt();

                SchemaSnapshot snapshot = new SchemaSnapshot(schemaVersion, className, recordSize, new HashMap<>());

                int fieldsSize = dataInputStream.readInt();

                for (int j = 0; j < fieldsSize; j++) {
                    String classType = dataInputStream.readUTF();
                    String name = dataInputStream.readUTF();
                    int fieldRecordSize = dataInputStream.readInt();
                    long offset = dataInputStream.readLong();

                    SchemaFieldSnapshot field = new SchemaFieldSnapshot(classType, name, fieldRecordSize, offset);

                    snapshot.getFields().put(name, field);
                }
                loaded.put(schemaVersion, snapshot);
            }
        }
        this.snapshots.clear();
        this.snapshots.putAll(loaded);
    }

    public boolean hasSnapshot(long schemaVersion) {
        return this.snapshots.containsKey(schemaVersion);
    }

    public SchemaSnapshot getSchemaSnapshot(long schemaVersion) {
        return this.snapshots.get(schemaVersion);
    }

    public SchemaSnapshot register(Schema<?> schema) {
        SchemaSnapshot snapshot = createSnapshot(schema);
        this.snapshots.put(snapshot.getSchemaVersion(), snapshot);
        return snapshot;
    }

    private SchemaSnapshot createSnapshot(Schema<?> schema) {
        SchemaSnapshot snapshot = new SchemaSnapshot();

        snapshot.setClassName(schema.getTypeClass().getName());
        snapshot.setSchemaVersion(schema.getSchemaVersion());
        snapshot.setRecordSize(schema.getRecordSize());

        Map<String, SchemaFieldSnapshot> fieldSnapshots = new HashMap<>();
        List<FieldSchema> fields = schema.getFields();
        fields.sort(Comparator.comparing(FieldSchema::getName));

        int offset = 0;
        for (FieldSchema field : fields) {
            SchemaFieldSnapshot fieldSnapshot = new SchemaFieldSnapshot();
            fieldSnapshot.setName(field.getName());
            fieldSnapshot.setRecordSize(field.getRecordSize());
            fieldSnapshot.setClassType(field.getType().getName());
            fieldSnapshot.setOffset(offset);
            fieldSnapshots.put(field.getName(), fieldSnapshot);

            offset += field.getRecordSize();
        }
        snapshot.setFields(fieldSnapshots);
        return snapshot;
    }

}
