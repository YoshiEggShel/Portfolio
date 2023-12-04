import sys
import fnmatch
import math
import hou

# Create necessary nodes
def create_nodes_needed():
    """
    Creates all the necessary nodes needed for the tool to start working.
    """
    # Check for Uv info
    check_uv()
    # Creates any missing attribute related nodes
    create_attrib_nodes()

    # Create the sequenceRead node
    create_sequenceRead_node()

    # Create groupbox and set valuews
    group_box()
    set_groupBox_expression()

    # Create and set up Vellum_feather asset
    create_vellum_asset()
    vellum_prep()

# Controls which mesh to use based on the Tpose to anim switch value
def tpose_to_anim():
    try:
        # Get the value of the t_pose_to_anim_switch
        switch_val = eval_asset_parm("tpose_to_anim")

        # Get the nodes
        curve_geo = get_parent_node("merge_curves")
        tpose_geo = get_parent_node("merge_skin")
        anim_geo = get_parent_node("animated_skin")
        groom_geo = tpose_geo.outputs()[0]
        # Get merge curves through groom geo
        m_curves = groom_geo.outputs()[0].path() + '/DISPLAY'
        # Get the guide groom node
        guide_groom = hou.node(groom_geo.outputs()[0].path())
        # Get vellum node
        vellum_node = hou.node("{0}/{1}_vellum".format(asset_parent_path(), asset_name()))

        # When the switch is toggled on, uses the animated geo. Otherwise uses t-pose mesh
        if switch_val == 0:
            set_asset_parm("vellum", 0)
            set_asset_parm("merge_curves", m_curves)
            curve_geo.setInput(2, tpose_geo)
            anim_geo.setDisplayFlag(0)
            tpose_geo.setDisplayFlag(1)
            groom_geo.setDisplayFlag(1)
            curve_geo.setDisplayFlag(0)
            vellum_node.setDisplayFlag(0)
        else:
            curve_geo.setInput(2, anim_geo)
            anim_geo.setDisplayFlag(1)
            tpose_geo.setDisplayFlag(0)
            groom_geo.setDisplayFlag(0)
            curve_geo.setDisplayFlag(1)
    except:
        set_asset_parm("tpose_to_anim", 0)
        hou.ui.displayMessage("Animated object path is not given")
        tpose_to_anim()

# Checks whether a uv map exists for the given mesh
def check_uv():
    skinpath = skin_path()[1]
    skin_node = hou.node(skinpath)
    geo = skin_node.geometry()
    uv = geo.findVertexAttrib("uv")
    if not uv:
        hou.ui.displayMessage("There is no uv in the geometry.")
        sys.exit()

# Controls functionality of visualising polygons for the feathers
def poly_vis():
    f_res = eval_asset_parm("fres")
    feather_res = eval_asset_parm("feather_segs")
    poly_res = eval_asset_parm("poly_res")
    poly_vis = eval_asset_parm("poly_vis_switch")

    if poly_vis == 0:
        set_asset_parm("feather_segs", f_res)
    else:
        set_asset_parm("feather_segs", poly_res)

# Backend functionality when switching visibility of polygon for feathers
def set_res():
    # Get resolution
    f_res = eval_asset_parm("fres")
    # Set it to feather_segs
    set_asset_parm("feather_segs", f_res)

# Backend functionality when switching visibility of polygon for feathers
def set_res_from_pres():
    # Get resolution
    poly_res = eval_asset_parm("poly_res")
    # Set it to feather_segs
    set_asset_parm("feather_segs", poly_res)

# Implemented for lighter performance during look dev phase
def group_box():
    # Get the geo path
    geo = hou.node(asset_parent_path())

    assetname = asset_name()
    box_name = assetname + ("_groupBox")

    # If box does not exist, create a box
    if check_if_node_exists(geo, box_name):
        return
    # Create box geo node
    box_geo = geo.createNode("geo", box_name)
    # Creates a box inside box_geo node
    box_geo.createNode("box")
    # Display the box as bounding box
    box_geo.parm("viewportlod").set(2)
    # Set displayFlag off
    box_geo.setDisplayFlag(0)

    # Move the newly created box to an appropriate position
    assetpath = asset_path()
    pos = hou.node(assetpath).position()
    # Create a vector 1 unit below in y
    box_pos = hou.Vector2(pos[0], pos[1] - 1)
    # Set the box_geo to box_pos position
    box_geo.setPosition(box_pos)

# Link the delete groupbox values to dyamic cube
def set_groupBox_expression():

    # Unlock the asset to allow editing
    asset = hou.node(".")
    asset.allowEditingOfContents()

    # Get assetpath
    assetpath = asset_path()

    # Get assetname
    assetname = asset_name()

    # Store the parms(sizex, sizey, sizez, and tx,ty,tz)
    parm_size_x = hou.parm(assetpath +  '/feather_tool/delete_groupbox/sizex')
    parm_size_y = hou.parm(assetpath + '/feather_tool/delete_groupbox/sizey')
    parm_size_z = hou.parm(assetpath + '/feather_tool/delete_groupbox/sizez')

    parm_translate_x = hou.parm(assetpath + '/feather_tool/delete_groupbox/tx')
    parm_translate_y = hou.parm(assetpath + '/feather_tool/delete_groupbox/ty')
    parm_translate_z = hou.parm(assetpath + '/feather_tool/delete_groupbox/tz')

    # Get the groupbox name
    box_name = assetname + "_groupBox"

    # Set the group box values as relative references to the delete node
    parm_size_x.setExpression('ch("../../../' + box_name + '/sx")')
    parm_size_y.setExpression('ch("../../../' + box_name + '/sy")')
    parm_size_z.setExpression('ch("../../../' + box_name + '/sz")')

    parm_translate_x.setExpression('ch("../../../' + box_name + '/tx")')
    parm_translate_y.setExpression('ch("../../../' + box_name + '/ty")')
    parm_translate_z.setExpression('ch("../../../' + box_name + '/tz")')

# Controls the group box switch
def group_switch():
    assetname = asset_name()

    # Get the group box node
    box_path = "/obj/" + assetname + "_groupBox"
    box_geo = hou.node(box_path)

    switch_val = eval_asset_parm("enable_group_box")

    if switch_val == 0:
        box_geo.setDisplayFlag(0)
        set_asset_parm("range_switch", 0)
    else:
        box_geo.setDisplayFlag(1)
        set_asset_parm("range_switch", 1)

# Controls how many clusters to create. Helps relieve rendering load
def create_clusters():
    # Delete any existing clusters
    delete_clusters()

    # Get the total number of delete nodes using the final cluster size parameter
    cluster_size = eval_asset_parm("cluster_core")
    loop_range = (cluster_size * 2) - 2
    in_cluster = hou.node(str(asset_path()) + '/feather_tool/IN_CLUSTER')
    cluster_switcher = hou.node(str(asset_path()) + '/feather_tool/cluster_switcher')
    move_left_node = hou.Vector2(-1, -1)
    move_right_node = hou.Vector2(1, -1)
    cluster_switcher.setPosition(in_cluster.position() + hou.Vector2(0, (math.log2(cluster_size) + 1) * -1))

    for i in range(loop_range):
        # Create the delete node
        del_node = create_asset_node("delete", "cluster_del0")
        # Set the operation to 'delete_by_range'
        set_child_node_parm(del_node, "groupop", 1)

        # Connection algorithm
        parent = int((i/2)) - 1
        parent_name = "cluster_del{}".format(parent)
        parent_path = str(asset_path()) + "/feather_tool/" + parent_name
        parent_node =  hou.node(parent_path)

        # Connect the nodes
        # If the node is the first or second delete node created
        # connect it directly to the IN_CLUSTER node
        right_position = None
        left_position = None
        if i <= 1:
            parent_node = in_cluster
            left_position = parent_node.position() + hou.Vector2(math.log2(cluster_size), -1)
            right_position = parent_node.position() + hou.Vector2(math.log2(cluster_size) * -1, -1)
        else:
            left_position = parent_node.position() + move_right_node
            right_position = parent_node.position() + move_left_node

        # Set the range start = 1 for odd nodes
        # Move the nodes for good formatting
        if i %2 != 0:
            set_child_node_parm(del_node, "rangestart", 1)
            del_node.setPosition(right_position)
        else:
            del_node.setPosition(left_position)

        # Connect the last row of del_nodes to the cluster_switcher
        if i >= (loop_range - cluster_size):
            cluster_switcher.setNextInput(del_node)

        # Make connection to parent node
        del_node.setInput(0, parent_node)

# Writes all cluster sequence to disk
def dynamic_cluster_sequence_write():
    # Get the path
    assetpath = asset_path()
    # Get cache directory path
    cache_dir = eval_asset_parm("cache_dir")
    # Get save to disk button
    save_btn = hou.parm(assetpath + '/execute')
    # Get cluster size from parameter
    cluster_size = eval_asset_parm("cluster_core")

    # Set feather mode to barb only
    set_asset_parm("feather_mode", 0)
    # Set the select cluster to 0 for looping purposes
    set_asset_parm("sel_cluster", 0)
    # Set trange to 'render frame range'
    set_asset_parm("trange", 1)

    for i in range(cluster_size):
        output_file = "{0}{1}_dynamicCluster{2}.$F4.bgeo.sc".format(cache_dir, asset_name(), i)
        set_asset_parm("sopoutput", output_file)
        set_asset_parm("sel_cluster", i)
        save_btn.pressButton()

    # Reset the select cluster to 0
    set_asset_parm("sel_cluster", 0)
    # Read clusters
    cluster_sequence_read()

# Writes shaft sequence to disk
def shaft_sequence_write():
    # Get the path
    assetpath = asset_path()
    # Get cache directory path
    cache_dir = eval_asset_parm("cache_dir")
    # Get save to disk button
    save_btn = hou.parm(assetpath + '/execute')

    # Set feather mode to shaft only
    set_asset_parm("feather_mode", 1)
    # Set trange to 'render frame range'
    set_asset_parm("trange", 1)

    output_file = "{0}{1}_shaft_sequence.$F4.bgeo.sc".format(cache_dir, asset_name())
    set_asset_parm("sopoutput", output_file)
    save_btn.pressButton()

    # Change file path to the newly created sequence file
    file_node = hou.node(assetpath + '_sequenceRead/shaft_sequence')
    hou.parm(file_node.path() + '/file').set(output_file)

# Writes static sequence to disk
def static_sequence_write():
    # Get the path
    assetpath = asset_path()
    # Get cache directory path
    cache_dir = eval_asset_parm("cache_dir")
    # Get save to disk button
    save_btn = hou.parm(assetpath + '/execute')

    # Set feather mode to barb only
    set_asset_parm("feather_mode", 0)
    # Set trange to 'render frame range'
    set_asset_parm("trange", 1)

    output_file = "{0}{1}_static_sequence.$F4.bgeo.sc".format(cache_dir, asset_name())
    set_asset_parm("sopoutput", output_file)
    save_btn.pressButton()

    # Change file path to the newly created sequence file
    file_node = hou.node(assetpath + '_sequenceRead/static_sequence')
    hou.parm(file_node.path() + '/file').set(output_file)

def create_sequenceRead_node():
    """
    Creates a sequenceRead node for cached files.
    """
    # Get asset name
    assetname = asset_name()
    # Get the name of the cluster_read geo node
    cluster_sequence_geo_name = assetname + "_sequenceRead"

    # Check whether the node alredy exists. If it exists nothing happens
    if check_if_node_exists(hou.node(asset_parent_path()), cluster_sequence_geo_name):
        return

    # Create the cluster_sequece_geo
    cluster_sequence_geo = hou.node("/obj").createNode("geo", cluster_sequence_geo_name)

    # Get the asset position
    asset_pos = hou.node(".").position()
    # Arrange the node position
    node_pos = hou.Vector2(asset_pos[0], asset_pos[1] - 2)

    # Set cluster_sequence_geo to its position
    cluster_sequence_geo.setPosition(node_pos)
    # Set display flag off
    cluster_sequence_geo.setDisplayFlag(0)

    # Create a merge node
    dynamic_merge = cluster_sequence_geo.createNode("merge", "dynamic_merge")
    # Create null Out node
    out_node = cluster_sequence_geo.createNode("null", "OUT")
    # Create static sequence_file
    static_sequence_file = cluster_sequence_geo.createNode('file', 'static_sequence')

    # Set the geometry file to blank
    static_sequence_file.parm('file').set("")
    # Set the missing geometry to 'no geometry'
    static_sequence_file.parm('missingframe').set(1)

    # Create shaft file
    shaft_file = cluster_sequence_geo.createNode('file', 'shaft_sequence')
    # Set the geometry file to blank
    shaft_file.parm('file').set("")
    # Set the missing geometry to 'no geometry'
    shaft_file.parm('missingframe').set(1)

    # Create a static merge node
    static_merge = cluster_sequence_geo.createNode('merge', 'static_merge')
    # Connect shaft file and static sequence file to static merge node
    static_merge.setNextInput(static_sequence_file)
    static_merge.setNextInput(shaft_file)

    # Create a shaft merge node
    shaft_merge = cluster_sequence_geo.createNode('merge', 'shaft_merge')
    # Connect shaft file and static sequence file to static merge node
    shaft_merge.setNextInput(dynamic_merge)
    shaft_merge.setNextInput(shaft_file)

    # Create a switcher node
    switch_node = cluster_sequence_geo.createNode('switch', 'switch')
    switch_node.setNextInput(shaft_merge)
    switch_node.setNextInput(static_merge)

    # Connect merge node to null node, and set display flag on null node
    out_node.setInput(0, switch_node)
    out_node.setDisplayFlag(1)
    out_node.setRenderFlag(1)
    cluster_sequence_geo.layoutChildren()

# cluster_sequence_read():
def cluster_sequence_read():
    """
    Deletes any existing dynamic cluster nodes and creates the appropriate amount of dynamic cluster nodes.
    It then sets the path of the file nodes to the created dyanmic cluster sequence files.
    """
    # Get the asset name
    assetname = asset_name()
    # get the asset_path
    assetpath = asset_path()
    # Get the sequence read node
    node = hou.node(assetpath + '_sequenceRead')
    # Get merge node
    merge_node = hou.node(assetpath + '_sequenceRead/dynamic_merge')
    # Get the cluster core size
    cluster_core_size = eval_asset_parm("cluster_core")
    # Get cache directory path
    cache_dir = eval_asset_parm("cache_dir")

    # Destroys any existing dynamic clusters in sequenceRead node
    for child in node.children():
        if fnmatch.fnmatchcase(child.name(), 'cluster*_*') == True:
            child.destroy()

    # Creates appropriate amount of dynamic cluster nodes
    for i in range(cluster_core_size):
        # Create file nodes
        file = node.createNode("file", "cluster_{0}".format(i))

        # Set the geometry file
        file_to_read = "{0}{1}_dynamicCluster{2}.$F4.bgeo.sc".format(cache_dir, asset_name(), i)
        hou.parm(file.path() + '/file').set(file_to_read)
        # Set the 'missing frame' parameter to 'no geometry'
        hou.parm(file.path() + '/missingframe').set(1)

        # Connect file node to merge node
        merge_node.setNextInput(file)

    # Layout all nodes in the sequenceRead node
    node.layoutChildren()

# Creates static cluster files
def create_static_cluster_files():
    # Delete existing cluster files
    del_cluster_files()

    # Get the cluster core size
    cluster_core_size = eval_asset_parm("cluster_core")
    # Get asset path
    assetpath = asset_path()
    # Get merge node
    merge_node = hou.node(assetpath + '/feather_tool/static_cluster_merge')

    # Define the positions
    cl_file_pos = hou.Vector2(-10.2755, 6.70171)
    cl_timeshift_pos = hou.Vector2(cl_file_pos[0], cl_file_pos[1] - 1)

    # Create file and timeshift nodes
    for i in range(cluster_core_size):
        # Create nodes
        cl_file = create_asset_node("file", "cl_file_0")
        cl_timeshift = create_asset_node("timeshift", "cl_timeshift0")

        # Set the positions
        cl_file.setPosition(cl_file_pos)
        cl_timeshift.setPosition(cl_timeshift_pos)

        # Delete the 'keyframe' channel of timeshift nodes
        hou.parm(cl_timeshift.path() + '/frame').deleteAllKeyframes()

        # Connect cl_file to cl_timeshift
        cl_timeshift.setInput(0, cl_file)
        # Connect timeshift to static_cluster_merge
        merge_node.setNextInput(cl_timeshift)

# Renders all clusters and sets up matrix calculation using first frame render
# Greatly increases performance for animation
def cluster_single_frame_write():
    # Create the necessary files
    create_static_cluster_files()

    # Get the path
    assetpath = asset_path()
    # Get cache directory path
    cache_dir = eval_asset_parm("cache_dir")
    # Get cluster size from parameter
    cluster_size = eval_asset_parm("cluster_core")
    # Get save to disk button
    save_btn = hou.parm(assetpath + '/execute')
    # Set feather mode to barb only
    set_asset_parm("feather_mode", 0)
    # Set the frame to the first frame
    hou.setFrame(1)
    # Set the select cluster to 0 for looping purposes
    set_asset_parm("sel_cluster", 0)
    # Set trange to 'render current frame'
    set_asset_parm("trange", 0)

    for i in range(cluster_size):
        output_file = "{0}{1}_staticCluster{2}.bgeo.sc".format(cache_dir, asset_name(), i)
        set_asset_parm("sopoutput", output_file)
        set_asset_parm("sel_cluster", i)
        save_btn.pressButton()

        # Read the file
        cl_file_node = hou.node(assetpath + '/feather_tool/cl_file_{0}'.format(i))
        set_child_node_parm(cl_file_node, "file", output_file)

    # Reset the select cluster to 0
    set_asset_parm("sel_cluster", 0)

# Turns on cluster view when dynamic_static toggle is turned on
def clusterView_switch():
    parm_dyna_tgl = eval_asset_parm("dyna_stat_tgl")

    if parm_dyna_tgl == 1:
        set_asset_parm("cluster_view", 1)

# Set up vellum asset paths
def vellum_prep():
    # Get the object merge nodes
    merge1 = str(asset_path()) + "_vellum/Vellum_ops/object_merge1/objpath1"
    merge2 = str(asset_path()) + "_vellum/Vellum_ops/object_merge2/objpath1"

    # Set the mergeCurves and animated skin from the asset to merge1 and merge 2 paths
    # Firstly, get the values from the asset path
    curves = eval_asset_parm("merge_curves")
    anim_skin = eval_asset_parm("animated_skin")

    # Sset these two to merge1 and merge2
    hou.parm(merge1).set(curves)
    hou.parm(merge2).set(anim_skin)

def vellum_on():
    """
    Controls behaviour of when vellum is toggled on.
    """
    # Get the value of the vellum toggle
    switch_val = eval_asset_parm("vellum")

    # Get the nodes
    curve_geo = get_parent_node("merge_curves")
    tpose_geo = get_parent_node("merge_skin")
    anim_geo = get_parent_node("animated_skin")
    groom_geo = tpose_geo.outputs()[0]
    # Get merge curves through groom geo
    m_curves = groom_geo.outputs()[0].path() + '/DISPLAY'
    # Get the guide groom node
    guide_groom = hou.node(groom_geo.outputs()[0].path())

    # Get the vellum node
    vellum_node = hou.node("{0}/{1}_vellum".format(asset_parent_path(), asset_name()))
    # Get the path to the OUT node inside vellum node
    vellum_out = vellum_node.path() + "/Vellum_ops/OUT"

    # If the vellum toggle is not on
    if switch_val == 0:
        set_asset_parm("merge_curves", m_curves)
        guide_groom.setDisplayFlag(1)
        vellum_node.setDisplayFlag(0)
    # If the vellum toggle is on
    else:
        set_asset_parm("merge_curves", vellum_out)
        curve_geo.setDisplayFlag(0)
        vellum_node.setDisplayFlag(1)

### Helper Functions ###
def asset_path():
    """
    Returns the path to the asset
    """
    path = hou.node(".").path()
    return path

def asset_parent_path():
    """
    Returns the path to the parent node of the asset
    """
    path = hou.node(".").path()
    return path.replace("/" + asset_name(), "")

def asset_name():
    """
    Returns the string of the name of the asset
    """
    name = str(hou.node("."))
    return name

def skin_path():
    """
    Returns the path to skin, the skin parameter values, the geo node of skin
    as an array of size 3
    """
    try:
        asset = asset_path()
        skin = hou.parm(asset + '/merge_skin').eval()
        skin_path = hou.node(skin).parent().path()
        skin_geo = hou.node(skin).parent()
        return skin_path, skin, skin_geo
    except:
        hou.ui.displayMessage("'Merge Skin' path cannot be empty")
        sys.exit()

def set_asset_parm(parm, val):
    """
    Sets the given parameter in the asset node to the given value.
    Parameters: parm(string), val(Any - depends on parameter)
    """
    assetpath = asset_path()
    parm_str = assetpath + ("/{0}".format(parm))
    hou.parm(parm_str).set(val)

def get_parent_node(parm):
    """
    Gets the node the parameter path is pointing to.
    """
    try:
        assetpath = asset_path()
        # Creates string to the path
        parm_str = assetpath + ("/{0}".format(parm))
        # Gets the path
        parm_path = hou.parm(parm_str).eval()
        # Gets the node
        parm_geo = hou.node(parm_path).parent()
        return parm_geo

    except:
        hou.ui.displayMessage("There is no such parameter: {0}".format(parm))
        sys.exit()

def eval_asset_parm(parm):
    """
    Returns the value of the parameter value
    """
    try:
        assetpath = asset_path()
        parm_str = assetpath + ("/{0}".format(parm))
        parm_eval = hou.parm(parm_str).eval()
        return parm_eval

    except:
        hou.ui.displayMessage("There is no such parameter: {0}".format(parm))
        sys.exit()

def create_asset_node(node, name):
    """
    Creates and returns a node with the given node type and name
    Parameters: node(string), name(string)
    """
    path = str(asset_path()) + "/feather_tool"
    node = hou.node(path).createNode("{0}".format(node), "{0}".format(name))
    return node

# Set the paramater value of the given node to the given value
def set_child_node_parm(node, parm_name, value):
    """
    Sets the given parameter to the given value of the child node inside the asset node.
    Parameter: node(string), parm_name(string), value(Any - depends on parameter)
    """
    path = str(asset_path()) + "/feather_tool"
    parm = path + ("/{0}/{1}".format(node, parm_name))
    hou.parm(parm).set(value)

def check_attrib_duplicate(dict):
    """
    Checks whether duplicate attribute nodes exists.
    Retuns a list of missing nodes. Empty list is returned if all necessary nodes are created.
    """
    # All nodes that currently exist in the skin geo
    child_nodes = skin_path()[2].children()

    for child in child_nodes:
        if child.name() in dict:
            dict[child.name()] = False

def get_attrib_dict():
    """
    Returns a set of attribute values that is needed for the tool
    """
    assetname = asset_name()
    attrib_dict = {
    assetname + "_attribcreate_PaintedRotation" : True,
    assetname + "_painted_rotation" : True,
    assetname + "_attribcreate_PaintedTilt" : True,
    assetname + "_painted_tilt" : True,
    assetname + "_attribcreate_PaintedRibbon" : True,
    assetname + "_painted_ribbon" : True,
    assetname + "_attribcreate_PaintedScale" : True,
    assetname + "_painted_scale" : True,
    assetname + "_attribcreate_PaintedRandScale" : True,
    assetname + "_painted_rand_scale" : True,
    assetname + "_attribcreate_PaintedConcave" : True,
    assetname + "_painted_concave" : True,
    assetname + "_OUT" : True
    }
    return attrib_dict

def create_attrib_nodes():
    """
    Creates all the necessary attribute nodes for the tool.
    If some nodes are missing, it creates the missing nodes.
    """
    # Checks whether attrib nodes have been generated already
    nodes_to_be_generated = get_attrib_dict()
    check_attrib_duplicate(nodes_to_be_generated)
    skinpath = skin_path()

    # Generating missing nodes
    for key in nodes_to_be_generated:
        if nodes_to_be_generated[key]:
            node = create_node(key)

    # Linking the nodes together
    connect_attrib_nodes(nodes_to_be_generated.keys())

    # Cleanly layout all nodes
    skinpath[2].layoutChildren()

    # Set the Merge Skin path to the newly created null node
    parm_str = asset_path() + "/merge_skin"
    hou.parm(parm_str).set(skinpath[0] + '/{0}_OUT'.format(asset_name()))

def create_node(name):
    """
    Creates a node using the given value..
    Parameters: name:string(name of the node being created)
    """
    assetname = asset_name()
    if name == assetname +"_attribcreate_PaintedRotation":
        node = attrib_create_node(name, "painted_rotation")
    elif name == assetname +"_painted_rotation":
        node = attrib_paint_node(name, "painted_rotation")
    elif name == assetname + "_attribcreate_PaintedTilt":
        node = attrib_create_node(name, "painted_tilt")
    elif name == assetname + "_painted_tilt":
        node = attrib_paint_node(name, "painted_tilt")
    elif name == assetname + "_attribcreate_PaintedRibbon":
        node = attrib_create_node(name, "painted_ribbon")
    elif name == assetname + "_painted_ribbon":
        node = attrib_paint_node(name, "painted_ribbon")
    elif name == assetname + "_attribcreate_PaintedScale":
        node = attrib_create_node(name, "painted_scale")
    elif name == assetname + "_painted_scale":
        node = attrib_paint_node(name, "painted_scale")
    elif name == assetname + "_attribcreate_PaintedRandScale":
        node = attrib_create_node(name, "painted_rand_scale")
    elif name == assetname + "_painted_rand_scale":
        node = attrib_paint_node(name, "painted_rand_scale")
    elif name == assetname + "_attribcreate_PaintedScale":
        node = attrib_create_node(name, "painted_tilt")
    elif name == assetname + "_painted_scale":
        node = attrib_paint_node(name, "painted_tilt")
    elif name == assetname + "_attribcreate_PaintedConcave":
        node = attrib_create_node(name, "painted_concave")
    elif name == assetname + "_painted_concave":
        node = attrib_paint_node(name, "painted_concave")
    elif name == assetname + "_OUT":
        # Create null node
        node = create_skin_node("null", "{0}_OUT".format(asset_name()))
        node.setDisplayFlag(True)
        node.setRenderFlag(True)
    else:
        hou.ui.displayMessage("There are no node settings for node named {0} in the codebase".format(name))
        sys.exit()
    return node

def connect_attrib_nodes(iterable):
    """
    Connects the necessary attrib nodes.
    Requires a null node named "OUT" in the merge skin path
    """
    skinpath = skin_path()[0]
    attach_node = hou.node(skinpath + "/OUT")
    for item in iterable:
        path = skinpath + "/{0}".format(item)
        node = hou.node(path)
        node.setInput(0, None)
        node.setNextInput(attach_node)
        attach_node = node

def attrib_create_node(node_name, attr_name):
    """
    Creates an attribute create node using the given vaues in the skin geometry.
    Parameters: node_name:string(name of the node being created), attr_name:string(attribute name)
    """
    node = create_skin_node("attribcreate", node_name)
    node.parm("name1").set(attr_name)
    node.parm("value1v1").set(1)
    return node

def attrib_paint_node(name, attr_name):
    """
    Creates an attribute paint node using the given vaues in the skin geometry.
    Parameters: node_name:string(name of the node being created), attr_name:string(attribute name)
    """
    node = create_skin_node("attribpaint", name)
    node.parm("attribname1").set(attr_name)
    return node

def check_if_node_exists(parent_node, name):
    """
    Checks whether a node with the given name exists as a child node of the given parent_node.
    Parameters: parent_node:node, name:string
    """
    # Query if node exists
    for child in parent_node.children():
        if child.name() == name:
            return True
    return False

def create_vellum_asset():
    """
    Creates vellum feather asset node.
    """
    if check_if_node_exists(hou.node(asset_parent_path()), asset_name() + "_vellum"):
        return
    # Get the position of the asset
    asset_pos = hou.node(".").position()
    # Create vellum asset
    vellum = hou.node(asset_parent_path()).createNode("vellum_feather", asset_name() + "_vellum")
    # Arrange the vellum_pos
    vellum_pos = hou.Vector2(asset_pos[0] - 3, asset_pos[1])
    # Set the position of vellum asset
    vellum.setPosition(vellum_pos)
    # Set vellum display flag off
    vellum.setDisplayFlag(0)

def del_cluster_files():
    """
    Deletes all existing cluster files.
    """
    assetpath = asset_path()
    node = hou.node(assetpath + '/feather_tool')

    for child in node.children():
        if fnmatch.fnmatchcase(child.name(), "cl_file*_*") == True or fnmatch.fnmatchcase(child.name(), "cl_timeshift**"):
            child.destroy()

def delete_clusters():
    """
    Deletes all cluster nodes inside the asset.
    """
    path = hou.node(str(asset_path()) + "/feather_tool")

    for child in path.children():
        if fnmatch.fnmatchcase(child.name(), "cluster*_del*") == True:
            child.destroy()
    delete_switcher_inputs("cluster_switcher")

# Delete the connection to switchers
def delete_switcher_inputs(switcher_name):
    """
    Deletes all inputs to the switcher of the given name inside the asset.
    Parameters: switcher_name:string(Name of switcher)
    """
    switcher = hou.node(str(asset_path()) + '/feather_tool/{0}'.format(switcher_name))
    for input in switcher.inputs():
        switcher.setInput(0, None)

def create_skin_node(node, name):
    """
    Creates a node inside the skin node the asset path is set to.
    Parameters: node:string(type of node), name:string(the name of the node being created)
    """
    skinpath = skin_path()
    try:
        node = hou.node(skinpath[0]).createNode("{0}".format(node), "{0}".format(name))
        return node
    except:
        hou.ui.displayMessage("Invalid type [{0}".format(node) + "] or invalid name [{0}".format(name) + "]")

def test():
    print(asset_parent_path())
