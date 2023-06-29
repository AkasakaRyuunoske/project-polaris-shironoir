package projectpolaris.ProjectPolarisShironoir.POJOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

@Table(value = "anaata")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Anaata {
    @PrimaryKeyColumn(name = "yume", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    String yume;

    @Column("message")
    String message;

    @Column("name")
    String name;

    @Column("probability")
    String probability;
}
