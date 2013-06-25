module api_remote {

    export interface ContentType {
        name: string;
        module: string;
        qualifiedName?: string;
        displayName: string;
        contentDisplayNameScript: string;
        superType: string;
        isAbstract: bool;
        isFinal: bool;
        allowChildren: bool;
        createdTime?: Date;
        modifiedTime?: Date;
        form: FormItem[];
    }

    export interface ContentTypeTreeNode extends ContentType {
        iconUrl:string;
        hasChildren:bool;
        contentTypes:ContentTypeTreeNode[];
    }

    export interface ContentTreeNode {
        allowsChildren:bool;
        contents:ContentTreeNode[];
        createdTime?:Date;
        deletable:bool;
        displayName:string;
        editable:bool;
        hasChildren:bool;
        iconUrl:string;
        id:string;
        modifiedTime?:Date;
        modifier:string;
        name:string;
        owner:string;
        path:string;
        type:string;
    }

    export interface FormItem {
        FormItemSet?: FormItemSet;
        Layout?: Layout;
        Input?: Input;
        MixinReference?: MixinReference;
    }

    export interface FormItemSet {
        name: string;
        label: string;
        immutable: bool;
        occurrences: Occurrences;
        customText: string;
        helpText: string;
        items: FormItem[];
    }

    export interface Layout {
        type: string;
        label: string;
        name: string;
        items: FormItem[];
    }

    export interface Input {
        name: string;
        label: string;
        immutable: bool;
        occurrences: Occurrences;
        indexed: bool;
        customText: string;
        validationRegexp?: string;
        helpText: string;
        config?: InputTypeConfig;
        type: InputType;
    }

    export interface MixinReference {
        name: string;
        reference: string;
        type: string;
    }

    export interface Occurrences {
        minimum: number;
        maximum: number;
    }

    export interface InputType {
        name: string;
        builtIn: bool;
    }

    export interface InputTypeConfig {
        relationshipType?: string;
        selectorType?: string;
        options?: {
            label: string;
            value: string;
        }[];
    }
}