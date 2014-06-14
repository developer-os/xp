module api.dom {

    export class PEl extends Element {

        constructor(className?: string) {
            super(new NewElementBuilder().setTagName("p").setClassName(className));
        }

        setText(value: string) {
            this.getEl().setInnerHtml(value)
        }
    }
}
