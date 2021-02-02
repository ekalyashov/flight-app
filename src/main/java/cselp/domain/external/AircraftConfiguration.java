package cselp.domain.external;


import java.io.Serializable;

public class AircraftConfiguration implements Serializable {

    private Long id;
    private Long typeId;
    private Long engineId;
    private String configuration;
    private String description;
    private Integer engineNum;
    private Integer identifierDiscrete;
    private AircraftType type;
    private EngineType engine;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    public Long getEngineId() {
        return engineId;
    }

    public void setEngineId(Long engineId) {
        this.engineId = engineId;
    }

    public String getConfiguration() {
        return configuration;
    }

    public void setConfiguration(String configuration) {
        this.configuration = configuration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getEngineNum() {
        return engineNum;
    }

    public void setEngineNum(Integer engineNum) {
        this.engineNum = engineNum;
    }

    public Integer getIdentifierDiscrete() {
        return identifierDiscrete;
    }

    public void setIdentifierDiscrete(Integer identifierDiscrete) {
        this.identifierDiscrete = identifierDiscrete;
    }

    public AircraftType getType() {
        return type;
    }

    public void setType(AircraftType type) {
        this.type = type;
    }

    public EngineType getEngine() {
        return engine;
    }

    public void setEngine(EngineType engine) {
        this.engine = engine;
    }
}
