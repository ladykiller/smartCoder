package com.wheat.code.pojo;

public class Column  extends AppObject{

    private String name;
    private String type;
    private int length;
    private String desc;
    private int nullable;
    private String defaultValue;
    private int precision;
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getNullable() {
        return nullable;
    }

    public void setNullable(int nullable) {
        this.nullable = nullable;
    }

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public int getPrecision() {
		return precision;
	}

	public void setPrecision(int precision) {
		this.precision = precision;
	}
    
    
}
