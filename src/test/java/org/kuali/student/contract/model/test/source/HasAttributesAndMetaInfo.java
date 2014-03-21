/**
 * Copyright 2004-2014 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.student.contract.model.test.source;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import org.kuali.student.contract.model.test.source.HasAttributesAndMeta;
import org.kuali.student.contract.model.test.source.Meta;

@SuppressWarnings("serial")
@XmlTransient
public abstract class HasAttributesAndMetaInfo extends HasAttributesInfo implements HasAttributesAndMeta, Serializable {

    @XmlElement
    private final MetaInfo meta;

    protected HasAttributesAndMetaInfo() {
        meta = null;
    }

    protected HasAttributesAndMetaInfo(HasAttributesAndMeta builder) {
        super(builder);
        this.meta= null != builder.getMeta() ? new MetaInfo.Builder(builder.getMeta()).build() : null;
    }

    @Override
    public MetaInfo getMeta() {
        return meta;
    }

    public static class Builder extends HasAttributesInfo.Builder implements HasAttributesAndMeta {

        private MetaInfo meta;

        public Builder() {}

        public Builder(HasAttributesAndMeta hasAMInfo) {
            super(hasAMInfo);
            
            if (null != hasAMInfo.getMeta()) {
	            MetaInfo.Builder builder = new MetaInfo.Builder();
	            builder.setCreateId(hasAMInfo.getMeta().getCreateId());
	            builder.setCreateTime(hasAMInfo.getMeta().getCreateTime());
	            builder.setUpdateId(hasAMInfo.getMeta().getUpdateId());
	            builder.setUpdateTime(hasAMInfo.getMeta().getUpdateTime());
	            builder.setVersionInd(hasAMInfo.getMeta().getVersionInd());
	            this.meta = builder.build();
            }
        }

        @Override
        public MetaInfo getMeta() {
            return meta;
        }

        public void setMetaInfo(MetaInfo meta) {
            this.meta = meta;
        }
    }
}
