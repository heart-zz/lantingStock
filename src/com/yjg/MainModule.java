package com.yjg;

import org.nutz.mvc.annotation.IocBy;
import org.nutz.mvc.annotation.Modules;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.ioc.provider.ComboIocProvider;

@Modules(scanPackage=true)
@Ok("json")
@IocBy(type=ComboIocProvider.class,
		args={"*org.nutz.ioc.loader.json.JsonLoader",
				"/ioc/",
				"*org.nutz.ioc.loader.annotation.AnnotationIocLoader",
				"com.yjg"
				})
public class MainModule {

}
