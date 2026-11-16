@file:OptIn(InternalResourceApi::class)

package campuscompass.shared.generated.resources

import kotlin.OptIn
import kotlin.String
import kotlin.collections.MutableMap
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.InternalResourceApi
import org.jetbrains.compose.resources.ResourceContentHash
import org.jetbrains.compose.resources.ResourceItem

private const val MD: String = "composeResources/campuscompass.shared.generated.resources/"

@delegate:ResourceContentHash(1_873_850_578)
internal val Res.drawable.campus_compass_banner: DrawableResource by lazy {
      DrawableResource("drawable:campus_compass_banner", setOf(
        ResourceItem(setOf(), "${MD}drawable/campus_compass_banner.png", -1, -1),
      ))
    }

@delegate:ResourceContentHash(-615_292_092)
internal val Res.drawable.campus_map_cabrillo_aptos: DrawableResource by lazy {
      DrawableResource("drawable:campus_map_cabrillo_aptos", setOf(
        ResourceItem(setOf(), "${MD}drawable/campus_map_cabrillo_aptos.png", -1, -1),
      ))
    }

@delegate:ResourceContentHash(883_132_533)
internal val Res.drawable.campus_map_csumb: DrawableResource by lazy {
      DrawableResource("drawable:campus_map_csumb", setOf(
        ResourceItem(setOf(), "${MD}drawable/campus_map_csumb.png", -1, -1),
      ))
    }

@delegate:ResourceContentHash(1_295_034_269)
internal val Res.drawable.campus_map_gavilan_gilroy: DrawableResource by lazy {
      DrawableResource("drawable:campus_map_gavilan_gilroy", setOf(
        ResourceItem(setOf(), "${MD}drawable/campus_map_gavilan_gilroy.png", -1, -1),
      ))
    }

@delegate:ResourceContentHash(218_409_262)
internal val Res.drawable.campus_map_hartnell_main: DrawableResource by lazy {
      DrawableResource("drawable:campus_map_hartnell_main", setOf(
        ResourceItem(setOf(), "${MD}drawable/campus_map_hartnell_main.png", -1, -1),
      ))
    }

@delegate:ResourceContentHash(22_846_074)
internal val Res.drawable.campus_map_mpc_marina: DrawableResource by lazy {
      DrawableResource("drawable:campus_map_mpc_marina", setOf(
        ResourceItem(setOf(), "${MD}drawable/campus_map_mpc_marina.png", -1, -1),
      ))
    }

@delegate:ResourceContentHash(-2_104_366_178)
internal val Res.drawable.campus_map_mpc_monterey: DrawableResource by lazy {
      DrawableResource("drawable:campus_map_mpc_monterey", setOf(
        ResourceItem(setOf(), "${MD}drawable/campus_map_mpc_monterey.png", -1, -1),
      ))
    }

@delegate:ResourceContentHash(-572_727_390)
internal val Res.drawable.campus_map_mpc_pstc: DrawableResource by lazy {
      DrawableResource("drawable:campus_map_mpc_pstc", setOf(
        ResourceItem(setOf(), "${MD}drawable/campus_map_mpc_pstc.png", -1, -1),
      ))
    }

@delegate:ResourceContentHash(-1_329_478_408)
internal val Res.drawable.campus_map_ucsc: DrawableResource by lazy {
      DrawableResource("drawable:campus_map_ucsc", setOf(
        ResourceItem(setOf(), "${MD}drawable/campus_map_ucsc.png", -1, -1),
      ))
    }

@delegate:ResourceContentHash(379_089_144)
internal val Res.drawable.compose_multiplatform: DrawableResource by lazy {
      DrawableResource("drawable:compose_multiplatform", setOf(
        ResourceItem(setOf(), "${MD}drawable/compose-multiplatform.xml", -1, -1),
      ))
    }

@delegate:ResourceContentHash(2_099_674_437)
internal val Res.drawable.logo: DrawableResource by lazy {
      DrawableResource("drawable:logo", setOf(
        ResourceItem(setOf(), "${MD}drawable/logo.jpg", -1, -1),
      ))
    }

@InternalResourceApi
internal fun _collectCommonMainDrawable0Resources(map: MutableMap<String, DrawableResource>) {
  map.put("campus_compass_banner", Res.drawable.campus_compass_banner)
  map.put("campus_map_cabrillo_aptos", Res.drawable.campus_map_cabrillo_aptos)
  map.put("campus_map_csumb", Res.drawable.campus_map_csumb)
  map.put("campus_map_gavilan_gilroy", Res.drawable.campus_map_gavilan_gilroy)
  map.put("campus_map_hartnell_main", Res.drawable.campus_map_hartnell_main)
  map.put("campus_map_mpc_marina", Res.drawable.campus_map_mpc_marina)
  map.put("campus_map_mpc_monterey", Res.drawable.campus_map_mpc_monterey)
  map.put("campus_map_mpc_pstc", Res.drawable.campus_map_mpc_pstc)
  map.put("campus_map_ucsc", Res.drawable.campus_map_ucsc)
  map.put("compose_multiplatform", Res.drawable.compose_multiplatform)
  map.put("logo", Res.drawable.logo)
}
