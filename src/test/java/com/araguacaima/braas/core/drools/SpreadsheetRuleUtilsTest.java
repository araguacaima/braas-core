package com.araguacaima.braas.core.drools;

import com.araguacaima.braas.core.SpreadsheetRuleUtils;
import com.araguacaima.braas.core.drools.model.forms.Form;
import com.araguacaima.braas.core.drools.model.forms.Question;
import com.araguacaima.braas.core.drools.model.forms.QuestionOption;
import com.araguacaima.braas.core.exception.InternalBraaSException;
import com.araguacaima.commons.utils.JsonUtils;
import org.junit.Test;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

public class SpreadsheetRuleUtilsTest {

    private static JsonUtils jsonUtils = new JsonUtils();
    private String fieldSeparator = "$$$###$$$";
    private String headerSeparator = "#$#$#$#$#";
    private String prefix = "form";
    private String matrixStr = "form.id$$$###$$$form.locale$$$###$$$form.title$$$###$$$form.description$$$###$$$form.url$$$###$$$form.questions.id$$$###$$$form.questions.title$$$###$$$form.questions.description$$$###$$$form.questions.type$$$###$$$form.questions.category$$$###$$$form.questions.options.id$$$###$$$form.questions.options.title$$$###$$$form.questions.options.description$$$###$$$form.questions.options.isText$$$###$$$form.questions.options.weighing$$$###$$$#$#$#$#$#$$$###$$$60bf396c-5c08-42a4-9d78-7100eb3c979c$$$###$$$ES$$$###$$$Primer paso$$$###$$$$$$###$$$$$$###$$$4b44037b-a78d-4a64-bd07-617f8111a9b3$$$###$$$Existe producto similar en el Banco que cubra esta funcionalidad$$$###$$$$$$###$$$SINGLE$$$###$$$CATEGORY_1$$$###$$$238418fd-82e9-4c31-af66-0cd669901141$$$###$$$Sí$$$###$$$$$$###$$$FALSE$$$###$$$4$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$d64bb541-2876-4cd1-ac79-0b39ea80f17e$$$###$$$No$$$###$$$$$$###$$$FALSE$$$###$$$0$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$6d02efd3-aad0-4fd8-8fe6-f983840e4e21$$$###$$$No lo sé$$$###$$$$$$###$$$FALSE$$$###$$$0$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$1339b2ed-5701-4e8f-bf83-72fbdb997ec4$$$###$$$Tipo de infraestrutura en la que se despliega el SaaS$$$###$$$$$$###$$$MULTIPLE$$$###$$$CATEGORY_2$$$###$$$76c3b14f-ce3a-4a1c-9d5b-23981cc2cf75$$$###$$$Cloud Pública (PaaS)$$$###$$$$$$###$$$FALSE$$$###$$$0$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$5144d90d-95f9-42ec-9c29-b045052ffef1$$$###$$$Cloud Privada$$$###$$$$$$###$$$FALSE$$$###$$$0$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$7e87702f-f886-4f23-a45a-cf83016140c3$$$###$$$Cloud Híbrida (PaaS)$$$###$$$$$$###$$$FALSE$$$###$$$0$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$48720ad6-fae7-4378-a848-d1a7f0f66637$$$###$$$On-premise (instalación específica, independientemente del tipo de datacenter o cloud)$$$###$$$$$$###$$$FALSE$$$###$$$0$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$424f19dc-a4ee-4402-827a-d0457173967c$$$###$$$Si su infraestructura reside total o parcialmente en la nube pública indique cuá(es):$$$###$$$$$$###$$$MULTIPLE$$$###$$$CATEGORY_1$$$###$$$ad2d5253-cce5-400e-a675-c6803863fb74$$$###$$$Amazon Web Services$$$###$$$$$$###$$$FALSE$$$###$$$0$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$80b79825-e5f3-4603-aeb1-e833a61a40d3$$$###$$$Google Cloud Platform$$$###$$$$$$###$$$FALSE$$$###$$$0$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$93b33067-da27-474b-81f7-a3be8926875c$$$###$$$Microsoft Azure$$$###$$$$$$###$$$FALSE$$$###$$$0$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$b7dec3d9-1f9a-4269-9e0f-2e50e42efec1$$$###$$$Otro:$$$###$$$$$$###$$$TRUE$$$###$$$0$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$96321bc1-00a3-4c61-8c5b-d54cadf677d1$$$###$$$Certificaciones de Seguridad acreditadas (PCI DSS, ISO 27001, SOC II, etc)$$$###$$$$$$###$$$MULTIPLE$$$###$$$CATEGORY_2$$$###$$$c473eccd-3dec-492a-bcb5-a19aa66a95d8$$$###$$$PCI DSS$$$###$$$$$$###$$$FALSE$$$###$$$0$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$d52f7c99-6a5e-4a1e-af20-57ba8076aff5$$$###$$$ISO 27001$$$###$$$$$$###$$$FALSE$$$###$$$0$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$ac86c2a4-8d27-455f-9715-3d3d89857239$$$###$$$SOC II$$$###$$$$$$###$$$FALSE$$$###$$$0$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$32ef91bd-ddc4-487d-a458-38c9f24b3f23$$$###$$$Otro:$$$###$$$$$$###$$$TRUE$$$###$$$0$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$a300bd8a-981e-4b39-a2dc-59aba319be1f$$$###$$$¿Se facilita Acceso a las Instalaciones y el Soporte necesario cuando por solicitud de algún ente regulador del Banco se requiera realizar una Auditoría a la Infraestructura del Producto?$$$###$$$$$$###$$$SINGLE$$$###$$$CATEGORY_2$$$###$$$59b2b495-5cf3-48c7-8068-f26105864a93$$$###$$$Sí$$$###$$$$$$###$$$FALSE$$$###$$$0$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$c70421fb-b3ca-4a90-8a64-752e7f579031$$$###$$$No$$$###$$$$$$###$$$FALSE$$$###$$$0$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$5b0507f9-7681-4400-8302-8d799dd42d1d$$$###$$$¿Permite autenticación delegada en los sistemas BBVA?$$$###$$$$$$###$$$SINGLE$$$###$$$CATEGORY_1$$$###$$$8b456ec9-3503-440e-a852-eb3282a98d12$$$###$$$Sí$$$###$$$$$$###$$$FALSE$$$###$$$0$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$afa38946-91c0-431e-b679-867cc814b2dd$$$###$$$No$$$###$$$$$$###$$$FALSE$$$###$$$0$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$35b626fa-160a-4169-901c-d31820ee3299$$$###$$$¿Se podría Automatizar la Gestión de Identidades desde el sistema IDM de BBVA (aprovisionamiento de usuarios / roles y asignación de los usuarios a los roles)?$$$###$$$$$$###$$$SINGLE$$$###$$$CATEGORY_1$$$###$$$09f79774-b072-424d-9c63-7a99a633e0cd$$$###$$$Sí$$$###$$$$$$###$$$FALSE$$$###$$$0$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$SINGLE$$$###$$$CATEGORY_1$$$###$$$557d998d-e450-4a5a-960a-dc0fd687b52d$$$###$$$No$$$###$$$$$$###$$$FALSE$$$###$$$0$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$7201dcf4-b09f-4b8c-804c-4dc1acb87ff8$$$###$$$¿Pueden segregarse las funciones de Administrador del Producto de las de Administrador de Seguridad? (siendo sólo éste el que tenga acceso a la Gestión de Identidades).$$$###$$$$$$###$$$SINGLE$$$###$$$CATEGORY_2$$$###$$$4a153bc2-ea0a-4087-9414-4ae88a0f47a1$$$###$$$Sí$$$###$$$$$$###$$$FALSE$$$###$$$0$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$00f5caa5-2ad5-4826-ad6a-1bcaeec95bf4$$$###$$$No$$$###$$$$$$###$$$FALSE$$$###$$$0$$$###$$$e122af1f-1a78-4245-864e-42ee1afbd43b$$$###$$$EN$$$###$$$First Step$$$###$$$$$$###$$$$$$###$$$4e5a67c4-b02e-45dd-be94-d675b622f22a$$$###$$$Is there a similar product in the Bank that covers this functionality?$$$###$$$$$$###$$$SINGLE$$$###$$$CATEGORY_1$$$###$$$cedc0392-3f51-4d6c-b811-1e04eb1ef348$$$###$$$Yes$$$###$$$$$$###$$$FALSE$$$###$$$0$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$653bebce-2b48-46d9-b7a5-442b4eaeb2da$$$###$$$No$$$###$$$$$$###$$$FALSE$$$###$$$0$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$86930cd6-432a-463c-bebf-33da85b2a5a7$$$###$$$I don't know$$$###$$$$$$###$$$FALSE$$$###$$$0$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$6b0af969-36ea-421b-a6ff-174c2290e607$$$###$$$Type of infrastructure in which the SaaS is deployed$$$###$$$$$$###$$$MULTIPLE$$$###$$$CATEGORY_2$$$###$$$95259b64-a4c3-4ba2-99dc-25c755ad7797$$$###$$$Public Cloud (PaaS)$$$###$$$$$$###$$$FALSE$$$###$$$0$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$9cfb7cca-9997-4814-afb2-b0a3d06e5d43$$$###$$$Private Cloud$$$###$$$$$$###$$$FALSE$$$###$$$0$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$75f75909-2a77-4fde-be2b-2f893435ec81$$$###$$$Hybrid Cloud (PaaS)$$$###$$$$$$###$$$FALSE$$$###$$$0$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$5d811c4d-57c5-4936-8ad9-273266d19250$$$###$$$On-premise (specific installation, regardless of the type of datacenter or cloud)$$$###$$$$$$###$$$FALSE$$$###$$$0$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$5c1454c6-663d-4df7-a4ef-8161a077594e$$$###$$$If your infrastructure resides totally or partially in the public cloud, indicate which:$$$###$$$$$$###$$$MULTIPLE$$$###$$$CATEGORY_1$$$###$$$ae474867-b888-4db5-b35b-d530a49186c1$$$###$$$Amazon Web Services$$$###$$$$$$###$$$FALSE$$$###$$$0$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$12c20470-c8a0-4411-b7bd-bb6139df2205$$$###$$$Google Cloud Platform$$$###$$$$$$###$$$FALSE$$$###$$$0$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$38cb270f-239d-411a-8a5b-0be7483fff44$$$###$$$Microsoft Azure$$$###$$$$$$###$$$FALSE$$$###$$$0$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$902fa5f1-f0ec-4124-b035-9bdd118b8ade$$$###$$$Other:$$$###$$$$$$###$$$TRUE$$$###$$$0$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$f5c3b769-95c8-4d36-9b1f-5e3bab8a6db8$$$###$$$Accredited Security Certifications (PCI DSS, ISO 27001, SOC II, etc.)$$$###$$$$$$###$$$MULTIPLE$$$###$$$CATEGORY_2$$$###$$$f1f3bbb2-c9e2-42f7-ae0b-d967f658b739$$$###$$$PCI DSS$$$###$$$$$$###$$$FALSE$$$###$$$0$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$fc9fbbd8-cdbc-4a1e-99b1-0cbd135e6d1a$$$###$$$ISO 27001$$$###$$$$$$###$$$FALSE$$$###$$$0$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$d0443bb4-4d38-4f82-b7f6-93dfdc4ad4d6$$$###$$$SOC II$$$###$$$$$$###$$$FALSE$$$###$$$0$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$65fdb489-7b58-488e-ab86-e9fd562e3d3c$$$###$$$Other:$$$###$$$$$$###$$$TRUE$$$###$$$0$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$dbdc003c-84c0-47c5-82a7-f234e397b33a$$$###$$$Is Access to the Installations and the necessary Support provided when, at the request of any regulatory entity of the Bank, an Audit of the Product Infrastructure is required?$$$###$$$$$$###$$$SINGLE$$$###$$$CATEGORY_2$$$###$$$9c25b39a-2252-43a9-a7e6-7d8ccbb3ed3e$$$###$$$Yes$$$###$$$$$$###$$$FALSE$$$###$$$0$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$8fe5ce7c-5b2e-4ac1-9332-9ebb37fd8fe8$$$###$$$No$$$###$$$$$$###$$$FALSE$$$###$$$0$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$316de46c-ea36-4439-b694-7c5ff13b97a1$$$###$$$Does it allow delegated authentication on BBVA systems?$$$###$$$$$$###$$$SINGLE$$$###$$$CATEGORY_1$$$###$$$5003adbe-5ab9-4957-8e41-c5fe29f36a0b$$$###$$$Yes$$$###$$$$$$###$$$FALSE$$$###$$$0$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$78db13e7-bfd0-4859-8c49-ede18210d93e$$$###$$$No$$$###$$$$$$###$$$FALSE$$$###$$$0$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$3c0fe11e-4d64-44ac-aa8f-78e9e000372b$$$###$$$Could Identity Management be automated from the BBVA IDM system (provisioning of users / roles and assigning users to roles)?$$$###$$$$$$###$$$SINGLE$$$###$$$CATEGORY_1$$$###$$$8f7551c2-530f-4ac6-836f-3e7e9dc608ce$$$###$$$Yes$$$###$$$$$$###$$$FALSE$$$###$$$0$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$SINGLE$$$###$$$CATEGORY_1$$$###$$$2a5c71a8-5178-4370-9c4e-ce335c158c6c$$$###$$$No$$$###$$$$$$###$$$FALSE$$$###$$$0$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$ee1d2e8e-8730-4e84-ac19-6e932e0ef977$$$###$$$Can the functions of Product Administrator be segregated from those of Security Administrator? (being only this one that has access to Identity Management).$$$###$$$$$$###$$$SINGLE$$$###$$$CATEGORY_2$$$###$$$4689b7ce-a5a0-4285-a0b9-fe01c75f4299$$$###$$$Yes$$$###$$$$$$###$$$FALSE$$$###$$$0$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$945ca280-729a-44e0-a9ce-998d642baf7c$$$###$$$No$$$###$$$$$$###$$$FALSE$$$###$$$0$$$###$$$a4ef5665-f777-4f69-8cfd-e84d246c04f4$$$###$$$ES$$$###$$$Segundo paso$$$###$$$$$$###$$$$$$###$$$d76c6fa7-31a5-44e3-9c14-f05b10bde716$$$###$$$Pregunta 1$$$###$$$$$$###$$$SINGLE$$$###$$$CATEGORY_1$$$###$$$3f596ead-7839-4db8-b045-32cc16811c4a$$$###$$$Sí$$$###$$$$$$###$$$FALSE$$$###$$$0$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$41f0bd08-648a-4c70-9e7b-e56219303c07$$$###$$$No$$$###$$$$$$###$$$FALSE$$$###$$$0$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$e42cf452-2593-4f7c-a150-027a0186b354$$$###$$$Pregunta 2$$$###$$$$$$###$$$MULTIPLE$$$###$$$CATEGORY_2$$$###$$$7eb57526-b4bd-4a42-9396-dacbf73274c8$$$###$$$A$$$###$$$$$$###$$$FALSE$$$###$$$0$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$8d05035e-424f-4680-938b-8c3fa878deaf$$$###$$$B$$$###$$$$$$###$$$FALSE$$$###$$$0$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$8ec07d17-6a93-4db8-9841-c3ce707e707f$$$###$$$C$$$###$$$$$$###$$$FALSE$$$###$$$0$$$###$$$4c7b9cc0-b9d4-4d9e-ab14-850c61df53de$$$###$$$EN$$$###$$$Second Step$$$###$$$$$$###$$$$$$###$$$1d828efc-5983-4c9f-8dec-31aed522d1eb$$$###$$$Question 1$$$###$$$$$$###$$$SINGLE$$$###$$$CATEGORY_1$$$###$$$b1f50f58-2387-486e-9b42-af00ed35ccbe$$$###$$$Yes$$$###$$$$$$###$$$FALSE$$$###$$$0$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$a8b59aec-ec15-44d4-9fe6-f67c1df60880$$$###$$$No$$$###$$$$$$###$$$FALSE$$$###$$$0$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$4c7b9cc0-b9d4-4d9e-ab14-850c61df53de$$$###$$$Question 2$$$###$$$$$$###$$$MULTIPLE$$$###$$$CATEGORY_2$$$###$$$9f2a20e4-f3ae-495d-b254-99d239295dc6$$$###$$$A$$$###$$$$$$###$$$FALSE$$$###$$$0$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$cd1c3a27-a2db-42d6-8a3a-9dea67305eb6$$$###$$$B$$$###$$$$$$###$$$FALSE$$$###$$$0$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$7e4529bb-5a34-4384-8c2c-5b470d054a69$$$###$$$C$$$###$$$$$$###$$$FALSE$$$###$$$0$$$###$$$7ff61900-cc7d-43f6-9eef-7b8d4eae3114$$$###$$$ES$$$###$$$Preguntas iniciales$$$###$$$$$$###$$$$$$###$$$638a93e9-0d6d-4d80-8b13-25ad232206b0$$$###$$$¿Qué tipo de requerimiento se intenta resolver?$$$###$$$$$$###$$$MULTIPLE$$$###$$$CATEGORY_1$$$###$$$16ba7561-6f0e-452f-8a8c-38ba3b9a061a$$$###$$$Procesamiento de datos$$$###$$$$$$###$$$FALSE$$$###$$$5$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$CATEGORY_2$$$###$$$ae1f9624-370f-43c0-bfa5-7d3f2d4da297$$$###$$$Predicción de comportamiento$$$###$$$$$$###$$$FALSE$$$###$$$5$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$CATEGORY_1$$$###$$$3c5a3650-5071-4e31-8621-9b8675ef3b6c$$$###$$$Transacción bancaria$$$###$$$$$$###$$$FALSE$$$###$$$5$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$CATEGORY_1$$$###$$$eba69599-e22f-46b6-b6bd-e162744f75c6$$$###$$$Colaboración con terceros$$$###$$$$$$###$$$FALSE$$$###$$$4$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$CATEGORY_2$$$###$$$180c09f0-67ec-4f23-bdab-d6332699907c$$$###$$$Gestión de eventos$$$###$$$$$$###$$$FALSE$$$###$$$3$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$CATEGORY_1$$$###$$$54127816-e45c-461b-b8b1-82b6762b1dfd$$$###$$$Cálculos$$$###$$$$$$###$$$FALSE$$$###$$$2$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$CATEGORY_2$$$###$$$b5bfb950-bccb-4580-97ea-82ca34327460$$$###$$$Consulta$$$###$$$$$$###$$$FALSE$$$###$$$1$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$d10e1059-e4e8-4ee4-b45c-e880c788a8cd$$$###$$$Tipos de datos a gestionar$$$###$$$$$$###$$$MULTIPLE$$$###$$$CATEGORY_2$$$###$$$aaf82452-ed8d-4423-a944-2452ecc5e6fc$$$###$$$Datos de cliente final$$$###$$$$$$###$$$FALSE$$$###$$$5$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$CATEGORY_1$$$###$$$72daee43-2b5a-44df-b894-3707bcbca3c2$$$###$$$No se manipulan datos$$$###$$$$$$###$$$FALSE$$$###$$$0$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$CATEGORY_1$$$###$$$c4b90e62-9080-4780-a89b-862ab128194d$$$###$$$Datos sensibles$$$###$$$$$$###$$$FALSE$$$###$$$4$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$CATEGORY_2$$$###$$$dee80de6-0d8d-4be0-926e-75b81aa4f8e3$$$###$$$Datos de empleados BBVA$$$###$$$$$$###$$$FALSE$$$###$$$2$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$CATEGORY_1$$$###$$$80816a0b-e8c5-4334-ab54-e5828ac74e38$$$###$$$Datos de partners$$$###$$$$$$###$$$FALSE$$$###$$$3$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$CATEGORY_2$$$###$$$4d7b0e0d-5f59-48a5-81da-54e566fbb1e5$$$###$$$Datos de proveedores$$$###$$$$$$###$$$FALSE$$$###$$$3$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$CATEGORY_2$$$###$$$aed2ce90-6599-436d-be72-6195840aaab3$$$###$$$Datos de terceros$$$###$$$$$$###$$$FALSE$$$###$$$3$$$###$$$dcb2d0d0-26a5-4f87-8e21-8af38f3624d8$$$###$$$EN$$$###$$$Initial questions$$$###$$$$$$###$$$$$$###$$$604be922-94b0-4b35-b6c0-218961faba6d$$$###$$$What type of requirement is being attempted?$$$###$$$$$$###$$$MULTIPLE$$$###$$$CATEGORY_1$$$###$$$25892bc8-cd1c-4cfd-8201-eb2fbd5da379$$$###$$$Data processing$$$###$$$$$$###$$$FALSE$$$###$$$5$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$CATEGORY_2$$$###$$$6f84c46a-e44e-4cbc-89f6-469191793f79$$$###$$$Behavior prediction$$$###$$$$$$###$$$FALSE$$$###$$$5$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$CATEGORY_1$$$###$$$66200660-c79a-4efd-84d9-d4b6537e7ae6$$$###$$$Banking transaction$$$###$$$$$$###$$$FALSE$$$###$$$5$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$CATEGORY_1$$$###$$$de02b311-32bd-4be5-a9ef-2b5f15e8987b$$$###$$$Collaboration with third parties$$$###$$$$$$###$$$FALSE$$$###$$$4$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$CATEGORY_2$$$###$$$09a34706-cb86-4be0-b22b-fa0ba013b1e7$$$###$$$Event management$$$###$$$$$$###$$$FALSE$$$###$$$3$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$CATEGORY_1$$$###$$$14fe1eab-598a-45c0-a798-c9c2e96a808a$$$###$$$Calculations$$$###$$$$$$###$$$FALSE$$$###$$$2$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$CATEGORY_2$$$###$$$65861bde-592f-4125-a8a3-f486203a343e$$$###$$$Query$$$###$$$$$$###$$$FALSE$$$###$$$1$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$b3520566-5e97-442b-9899-6be3852e1ae1$$$###$$$Types of data to manage$$$###$$$$$$###$$$MULTIPLE$$$###$$$CATEGORY_2$$$###$$$ac1f5288-f694-4f67-9e7b-9e4df4fe491f$$$###$$$End Customer Data$$$###$$$$$$###$$$FALSE$$$###$$$5$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$CATEGORY_1$$$###$$$5fbecf7e-7642-4d4d-9031-3f5b26794996$$$###$$$No data is manipulated.$$$###$$$$$$###$$$FALSE$$$###$$$0$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$CATEGORY_1$$$###$$$c2327292-6142-4e69-aa57-2d85d13a2a94$$$###$$$Sensitive data$$$###$$$$$$###$$$FALSE$$$###$$$4$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$CATEGORY_2$$$###$$$943c0696-dac1-4af4-958e-ec9cf2397c0d$$$###$$$BBVA employee data$$$###$$$$$$###$$$FALSE$$$###$$$2$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$CATEGORY_1$$$###$$$3e3a3f1e-dc62-4beb-8b26-88986b8f4cfc$$$###$$$Partner Data$$$###$$$$$$###$$$FALSE$$$###$$$3$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$CATEGORY_2$$$###$$$3ebf1026-342f-4c50-b100-d3cfd8e6c8ad$$$###$$$Supplier data$$$###$$$$$$###$$$FALSE$$$###$$$3$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$$$$###$$$CATEGORY_2$$$###$$$e8f16770-b70e-4688-8ff1-bdfaf4d5f812$$$###$$$Third party data$$$###$$$$$$###$$$FALSE$$$###$$$3";

/*    @Test
    public void testBuildMatrix() {
        String q = "238418fd-82e9-4c31-af66-0cd669901141,d64bb541-2876-4cd1-ac79-0b39ea80f17e,6d02efd3-aad0-4fd8-8fe6-f983840e4e21,76c3b14f-ce3a-4a1c-9d5b-23981cc2cf75,5144d90d-95f9-42ec-9c29-b045052ffef1,7e87702f-f886-4f23-a45a-cf83016140c3,48720ad6-fae7-4378-a848-d1a7f0f66637,ad2d5253-cce5-400e-a675-c6803863fb74,80b79825-e5f3-4603-aeb1-e833a61a40d3,93b33067-da27-474b-81f7-a3be8926875c,b7dec3d9-1f9a-4269-9e0f-2e50e42efec1,c473eccd-3dec-492a-bcb5-a19aa66a95d8,d52f7c99-6a5e-4a1e-af20-57ba8076aff5,ac86c2a4-8d27-455f-9715-3d3d89857239,32ef91bd-ddc4-487d-a458-38c9f24b3f23,59b2b495-5cf3-48c7-8068-f26105864a93,c70421fb-b3ca-4a90-8a64-752e7f579031,8b456ec9-3503-440e-a852-eb3282a98d12,afa38946-91c0-431e-b679-867cc814b2dd,09f79774-b072-424d-9c63-7a99a633e0cd,557d998d-e450-4a5a-960a-dc0fd687b52d,4a153bc2-ea0a-4087-9414-4ae88a0f47a1,00f5caa5-2ad5-4826-ad6a-1bcaeec95bf4,cedc0392-3f51-4d6c-b811-1e04eb1ef348,653bebce-2b48-46d9-b7a5-442b4eaeb2da,86930cd6-432a-463c-bebf-33da85b2a5a7,95259b64-a4c3-4ba2-99dc-25c755ad7797,9cfb7cca-9997-4814-afb2-b0a3d06e5d43,75f75909-2a77-4fde-be2b-2f893435ec81,5d811c4d-57c5-4936-8ad9-273266d19250,ae474867-b888-4db5-b35b-d530a49186c1,12c20470-c8a0-4411-b7bd-bb6139df2205,38cb270f-239d-411a-8a5b-0be7483fff44,902fa5f1-f0ec-4124-b035-9bdd118b8ade,f1f3bbb2-c9e2-42f7-ae0b-d967f658b739,fc9fbbd8-cdbc-4a1e-99b1-0cbd135e6d1a,d0443bb4-4d38-4f82-b7f6-93dfdc4ad4d6,65fdb489-7b58-488e-ab86-e9fd562e3d3c,9c25b39a-2252-43a9-a7e6-7d8ccbb3ed3e,8fe5ce7c-5b2e-4ac1-9332-9ebb37fd8fe8,5003adbe-5ab9-4957-8e41-c5fe29f36a0b,78db13e7-bfd0-4859-8c49-ede18210d93e,8f7551c2-530f-4ac6-836f-3e7e9dc608ce,2a5c71a8-5178-4370-9c4e-ce335c158c6c,4689b7ce-a5a0-4285-a0b9-fe01c75f4299,945ca280-729a-44e0-a9ce-998d642baf7c,3f596ead-7839-4db8-b045-32cc16811c4a,41f0bd08-648a-4c70-9e7b-e56219303c07,7eb57526-b4bd-4a42-9396-dacbf73274c8,8d05035e-424f-4680-938b-8c3fa878deaf,8ec07d17-6a93-4db8-9841-c3ce707e707f,b1f50f58-2387-486e-9b42-af00ed35ccbe,a8b59aec-ec15-44d4-9fe6-f67c1df60880,9f2a20e4-f3ae-495d-b254-99d239295dc6,cd1c3a27-a2db-42d6-8a3a-9dea67305eb6,7e4529bb-5a34-4384-8c2c-5b470d054a69,16ba7561-6f0e-452f-8a8c-38ba3b9a061a,ae1f9624-370f-43c0-bfa5-7d3f2d4da297,3c5a3650-5071-4e31-8621-9b8675ef3b6c,eba69599-e22f-46b6-b6bd-e162744f75c6,180c09f0-67ec-4f23-bdab-d6332699907c,54127816-e45c-461b-b8b1-82b6762b1dfd,b5bfb950-bccb-4580-97ea-82ca34327460,aaf82452-ed8d-4423-a944-2452ecc5e6fc,72daee43-2b5a-44df-b894-3707bcbca3c2,c4b90e62-9080-4780-a89b-862ab128194d,dee80de6-0d8d-4be0-926e-75b81aa4f8e3,80816a0b-e8c5-4334-ab54-e5828ac74e38,4d7b0e0d-5f59-48a5-81da-54e566fbb1e5,aed2ce90-6599-436d-be72-6195840aaab3,25892bc8-cd1c-4cfd-8201-eb2fbd5da379,6f84c46a-e44e-4cbc-89f6-469191793f79,66200660-c79a-4efd-84d9-d4b6537e7ae6,de02b311-32bd-4be5-a9ef-2b5f15e8987b,09a34706-cb86-4be0-b22b-fa0ba013b1e7,14fe1eab-598a-45c0-a798-c9c2e96a808a,65861bde-592f-4125-a8a3-f486203a343e,ac1f5288-f694-4f67-9e7b-9e4df4fe491f,5fbecf7e-7642-4d4d-9031-3f5b26794996,c2327292-6142-4e69-aa57-2d85d13a2a94,943c0696-dac1-4af4-958e-ec9cf2397c0d,3e3a3f1e-dc62-4beb-8b26-88986b8f4cfc,3ebf1026-342f-4c50-b100-d3cfd8e6c8ad,e8f16770-b70e-4688-8ff1-bdfaf4d5f812,4,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,5,5,5,4,3,2,1,5,0,4,2,3,3,3,5,5,5,4,3,2,1,5,0,4,2,3,3,3";
        Map qMap = SpreadsheetRuleUtils.buildMatrix(q);
        Assert.assertEquals(qMap.get("238418fd-82e9-4c31-af66-0cd669901141"), "4");
        Assert.assertEquals(qMap.get("e8f16770-b70e-4688-8ff1-bdfaf4d5f812"), "3");
    }*/

    @Test
    public void testStringArrayToBean() throws InternalBraaSException, IOException {
        SpreadsheetRuleUtils spreadsheetRuleUtils = new SpreadsheetRuleUtils(matrixStr, fieldSeparator, headerSeparator);
        Collection<Form> result = spreadsheetRuleUtils.stringArrayToBeans(prefix, Form.class);
        System.out.println(jsonUtils.toJSON(result));
    }

    @Test
    public void testFixMatrix() throws InternalBraaSException, IOException {
        SpreadsheetRuleUtils spreadsheetRuleUtils = new SpreadsheetRuleUtils(matrixStr, fieldSeparator, headerSeparator);
        String[] result = spreadsheetRuleUtils.buildMatrix();
        System.out.println(jsonUtils.toJSON(result));
    }

    @Test
    public void testGetIntervals() throws InternalBraaSException, IOException {
        SpreadsheetRuleUtils spreadsheetRuleUtils = new SpreadsheetRuleUtils(matrixStr, fieldSeparator, headerSeparator);
        LinkedList<SpreadsheetRuleUtils.Interval> result = spreadsheetRuleUtils.getIntervals();
        System.out.println(jsonUtils.toJSON(result));
    }

    @Test
    public void testGetIntervals2() throws InternalBraaSException, IOException {
        SpreadsheetRuleUtils spreadsheetRuleUtils = new SpreadsheetRuleUtils(matrixStr, fieldSeparator, headerSeparator);
        LinkedList<SpreadsheetRuleUtils.Interval> result = spreadsheetRuleUtils.getIntervals(5, 344);
        System.out.println(jsonUtils.toJSON(result));
    }

    @Test
    public void testFindOption() throws InternalBraaSException {
        SpreadsheetRuleUtils spreadsheetRuleUtils = new SpreadsheetRuleUtils(matrixStr, fieldSeparator, headerSeparator);
        Collection<Form> result = spreadsheetRuleUtils.stringArrayToBeans(prefix, Form.class);
        //QuestionOption option = $1;
        String optionId = "25892bc8-cd1c-4cfd-8201-eb2fbd5da379";
        QuestionOption optionFound = null;
        double accumulatedWeighting = 0;
        for (Form form : result) {
            Collection<Question> questions = form.getQuestions();
            for (Question question : questions) {
                Collection<QuestionOption> options = question.getOptions();
                for (QuestionOption option_ : options) {
                    if (option_.getId().equals(optionId)) {
                        optionFound = option_;
                        break;
                    }
                }
            }
        }

        if (optionFound != null) {
            accumulatedWeighting = accumulatedWeighting + optionFound.getWeighing();
        }


    }
}
