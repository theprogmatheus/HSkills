package com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.lib.flatstorage;

import com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.lib.flatstorage.schema.FieldSchema;
import com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.lib.flatstorage.schema.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
public class StorageSchema extends StorageData {

    private final Map<Long, SchemaSnapshot> snapshots;

    public StorageSchema() {
        this.snapshots = new HashMap<>();
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeInt(this.snapshots.size()); // tamanho do mapa.
        for (SchemaSnapshot snapshot : this.snapshots.values()) {

            output.writeUTF(snapshot.getClassName()); // salvar o nome da classe
            output.writeLong(snapshot.getSchemaVersion()); // salva a versão do schema
            output.writeInt(snapshot.getRecordSize()); // salva o record size

            Map<String, SchemaFieldSnapshot> fields = snapshot.getFields();

            output.writeInt(fields.size()); // salva o tamanho do mapa dos fields
            for (SchemaFieldSnapshot field : fields.values()) {
                output.writeUTF(field.getClassType()); // salva o tipo do field (acho que nem precisa, considerando o controle de versão)
                output.writeUTF(field.getName()); // salva o nome do campo
                output.writeInt(field.getRecordSize()); // salva o tamanho do registro (necessário aqui?, vou manter)
                output.writeLong(field.getOffset());// salva o ponteiro para o campo.  depois  para encontrar o dado do field é só usar offset + pointer
            }
        }
    }

    @Override
    public void read(DataInput input) throws IOException {
        Map<Long, SchemaSnapshot> loaded = new HashMap<>();

        int size = input.readInt();
        for (int i = 0; i < size; i++) {
            String className = input.readUTF();
            long schemaVersion = input.readLong();
            int recordSize = input.readInt();

            SchemaSnapshot snapshot = new SchemaSnapshot(schemaVersion, className, recordSize, new HashMap<>());

            int fieldsSize = input.readInt();

            for (int j = 0; j < fieldsSize; j++) {
                String classType = input.readUTF();
                String name = input.readUTF();
                int fieldRecordSize = input.readInt();
                long offset = input.readLong();

                SchemaFieldSnapshot field = new SchemaFieldSnapshot(classType, name, fieldRecordSize, offset);

                snapshot.getFields().put(name, field);
            }
            loaded.put(schemaVersion, snapshot);
        }
        this.snapshots.clear();
        this.snapshots.putAll(loaded);
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
