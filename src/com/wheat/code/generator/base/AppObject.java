package com.wheat.code.generator.base;

import com.wheat.code.inter.Resource;
import com.wheat.code.utils.JsonUtil;
import com.wheat.code.utils.StringUtil;

public class AppObject implements Resource{
	private String resourceName;

    public String getJson(){
        return JsonUtil.toJson(this);
    }
    
    @Override
    public String toString() {
        return StringUtil.concat(this.getClass().getSimpleName(),this.getJson());
    }

	@Override
	public Resource decode(String content) {
		throw new RuntimeException((getNotSuport("decode")));
	}

	@Override
	public String encode() {
		return this.getJson();
	}

	@Override
	public void fromJson(String json) {
		throw new RuntimeException((getNotSuport("fromJson")));
	}

	@Override
	public String getKey() {
		throw new RuntimeException((getNotSuport("getKey")));
	}

	@Override
	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	private String getNotSuport(String content){
        return StringUtil.concat("Resource:",getResourceName(), " not suport ", " ",content);
    }	

}