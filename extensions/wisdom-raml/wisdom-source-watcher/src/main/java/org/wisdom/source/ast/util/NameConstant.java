/*
 * #%L
 * Wisdom-Framework
 * %%
 * Copyright (C) 2013 - 2015 Wisdom Framework
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.wisdom.source.ast.util;

import org.wisdom.api.annotations.*;

/**
 * @author barjo
 */
public interface NameConstant {
    String ANNOTATION_PATH = Path.class.getSimpleName();
    String ANNOTATION_ROUTE = Route.class.getSimpleName();
    String ROUTE_ACCEPTS = "accepts";
    String ROUTE_PRODUCES = "produces";
    String ANNOTATION_PARAM = org.wisdom.api.annotations.Parameter.class.getSimpleName();
    String ANNOTATION_PATH_PARAM = PathParameter.class.getSimpleName();
    String ANNOTATION_QUERYPARAM = QueryParameter.class.getSimpleName();
    String ANNOTATION_FORMPARAM = FormParameter.class.getSimpleName();
    String ANNOTATION_BODY = Body.class.getSimpleName();
    String ANNOTATION_DEFAULTVALUE = DefaultValue.class.getSimpleName();
    String DOC_BODY_SAMPLE = "@body.sample";
}