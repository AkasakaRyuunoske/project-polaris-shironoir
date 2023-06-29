package projectpolaris.ProjectPolarisShironoir.Repository;

import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;
import projectpolaris.ProjectPolarisShironoir.POJOs.Anaata;

@Repository
public interface AnaataRepository extends CassandraRepository<Anaata, String> {
    @AllowFiltering
    Anaata findByYume(String anaata);
}
