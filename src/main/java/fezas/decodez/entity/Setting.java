package fezas.decodez.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Objects;

@Entity
@Data
@Table(name = "SETTING", schema = "PUBLIC", catalog = "DECODEZ")
public class Setting {
    @Id
    @Column(name = "ID", nullable = false)
    private long id;
    @Basic
    @Column(name = "INPUT_DIRECTORY", nullable = false, length = 254)
    private String inputDirectory;
    @Basic
    @Column(name = "OUTPUT_DIRECTORY", nullable = false, length = 254)
    private String outputDirectory;
    @Basic
    @Column(name = "TIME_DECODE", nullable = false)
    private int timeDecode;
    @Basic
    @Column(name = "AUTOSCAN", nullable = false)
    private boolean autoscan;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Setting setting = (Setting) o;
        return id == setting.id && timeDecode == setting.timeDecode && autoscan == setting.autoscan && Objects.equals(inputDirectory, setting.inputDirectory) && Objects.equals(outputDirectory, setting.outputDirectory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, inputDirectory, outputDirectory, timeDecode, autoscan);
    }

    @Override
    public String toString() {
        return "Setting{" +
                "id=" + id +
                ", inputDirectory='" + inputDirectory + '\'' +
                ", outputDirectory='" + outputDirectory + '\'' +
                ", timeDecode=" + timeDecode +
                ", autoscan=" + autoscan +
                '}';
    }
}
