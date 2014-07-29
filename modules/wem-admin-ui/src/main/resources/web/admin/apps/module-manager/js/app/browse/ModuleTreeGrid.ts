module app.browse {

    import GridColumn = api.ui.grid.GridColumn;
    import GridColumnBuilder = api.ui.grid.GridColumnBuilder;

    import ModuleSummary = api.module.ModuleSummary;
    import ModuleSummaryViewer = api.module.ModuleSummaryViewer;
    import TreeGrid = api.ui.treegrid.TreeGrid;
    import TreeNode = api.ui.treegrid.TreeNode;
    import TreeGridBuilder = api.ui.treegrid.TreeGridBuilder;
    import DateTimeFormatter = api.ui.treegrid.DateTimeFormatter;
    import TreeItem = api.ui.treegrid.TreeItem;

    export class ModuleTreeGrid extends TreeGrid<ModuleSummary> {

        constructor() {
            super(new TreeGridBuilder<ModuleSummary>().
                    setColumns([
                        new GridColumnBuilder<TreeNode<ModuleSummary>>().
                            setName("Name").
                            setId("displayName").
                            setField("displayName").
                            setFormatter(this.defaultNameFormatter).
                            setMinWidth(250).
                            build(),

                        new GridColumnBuilder<TreeNode<ModuleSummary>>().
                            setName("ModifiedTime").
                            setId("modifiedTime").
                            setField("modifiedTime").
                            setWidth(150).
                            setMinWidth(150).
                            setFormatter(DateTimeFormatter.format).
                            build() ,

                        new GridColumnBuilder<TreeNode<ModuleSummary>>().
                            setName("Version").
                            setId("version").
                            setField("version").
                            setMaxWidth(70).
                            setMinWidth(50).
                            build() ,

                        new GridColumnBuilder<TreeNode<ModuleSummary>>().
                            setName("State").
                            setId("state").
                            setField("state").
                            setMinWidth(80).
                            build()

                    ]).prependClasses("module-grid")
            );
        }

        fetchChildren(parent?: ModuleSummary): Q.Promise<ModuleSummary[]> {
            return new api.module.ListModulesRequest().sendAndParse();
        }

        private defaultNameFormatter(row: number, cell: number, value: any, columnDef: any, node: TreeNode<ModuleSummary>) {
            var viewer = new ModuleSummaryViewer();
            viewer.setObject(node.getData());
            return viewer.toString();
        }
    }
}
