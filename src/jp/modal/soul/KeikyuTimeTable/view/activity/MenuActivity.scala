package jp.modal.soul.KeikyuTimeTable.view.activity

import android.content.res.Configuration
import android.os.Bundle
import android.widget.GridView
import jp.modal.soul.KeikyuTimeTable.R
import jp.modal.soul.KeikyuTimeTable.migration.AppMigration
import jp.modal.soul.KeikyuTimeTable.view.adapter.MenuAdapter

/**
 * Created by imae on 2015/04/07.
 */
class MenuActivity extends BaseActivity {
  override def onCreate(bundle: Bundle) {
    super.onCreate(bundle)
    setContentView(R.layout.menu_activity)

    AppMigration(getApplicationContext).check()

    setupTopImages

    setupMenuGrid
  }

  /**
   * 画面の向きに合わせたトップ画像の回転処理
   */
  def setupTopImages: Unit ={
    if(getResources.getConfiguration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
      findViewById(R.id.busstop_img).setRotation(90)
      findViewById(R.id.bus_side_img).setRotation(90)
      findViewById(R.id.kemuri_img).setRotation(90)
    }
  }

  /**
   * メニューグリッドのセットアップ
   */
  def setupMenuGrid: Unit = {
    val menuGrid = findMyViewById[GridView](R.id.menu_grid).get
    menuGrid.setAdapter(new MenuAdapter()(this))
  }
}
