--
-- Copyright 2005-2012 The Kuali Foundation
--
-- Licensed under the Educational Community License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
--
-- http://www.opensource.org/licenses/ecl2.php
--
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
--

-- select * from 
-- KREW_RTE_NODE_T
-- order by rte_node_id desc
-- 
-- SELECT sequence_name, last_number
-- FROM user_sequences
-- where sequence_name = 'KREW_RTE_NODE_S'

drop sequence KREW_RTE_NODE_S
/
create sequence KREW_RTE_NODE_S start with 13009
/

-- SELECT  KREW_RTE_NODE_S.nextval
-- from KREW_RTE_NODE_T
-- where rownum < (3009 + 3 - 2971)
-- /

-- select * 
-- from KREW_RTE_NODE_CFG_PARM_T
-- order by rte_node_cfg_parm_id desc
-- 
-- SELECT sequence_name, last_number
-- FROM user_sequences
-- where sequence_name = 'KREW_RTE_NODE_CFG_PARM_S'

drop sequence KREW_RTE_NODE_CFG_PARM_S
/
create sequence KREW_RTE_NODE_CFG_PARM_S start with 12833
/

-- SELECT  KREW_RTE_NODE_CFG_PARM_S.nextval
-- from KREW_RTE_NODE_CFG_PARM_T
-- where rownum < (2833 + 3 - 2656)
-- /

-- select * from 
-- KREW_DOC_TYP_ATTR_T
-- order by DOC_TYP_ATTRIB_ID desc
-- 
-- SELECT sequence_name, last_number
-- FROM user_sequences
-- where sequence_name = 'KREW_DOC_TYP_ATTR_S'
