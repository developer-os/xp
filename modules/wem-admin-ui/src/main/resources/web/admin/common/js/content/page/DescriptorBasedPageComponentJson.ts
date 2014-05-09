module api.content.page {

    export interface DescriptorBasedPageComponentJson extends PageComponentJson {

        descriptor:string;

        config: api.data.json.DataTypeWrapperJson[];
    }
}