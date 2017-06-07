package com.wwr.clock;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.mpw.constant.Constant;
import com.wwr.clock.temp.GetTempNo;

public class IndoorTempView extends View {

    public static final String KEY_ID = "id";

    public static final int ID_FROM_INDOOR = 100;
    public static final int ID_FROM_OUTDOOR = 101;

	private Paint grayPAint;
	private Paint blackPAint;
	private Paint bluePAint;
	private Paint blackPath;// 画华氏度的曲线的线
	private Paint redPAint; // 画温度曲线的线
	private Paint paintData;// 绘制温度点的画笔
	private Paint paintF;// 绘制温度点的画笔
	public static Thread thread;

	private boolean isC = true;

	private GetTempNo gt;
	private Context context;
	// 获得屏幕的长宽
	WindowManager wm = (WindowManager) getContext().getSystemService(
			Context.WINDOW_SERVICE);
	int width = wm.getDefaultDisplay().getWidth(); // 屏幕的长度

	int height = wm.getDefaultDisplay().getHeight(); // 屏幕的高度

	int SX = width * 1 / 10; // X轴的起始点
	int EX = width * 14 / 15; // X的结束点
	int GAP = (EX - SX - 50) / 9; // 每个点的距离
	int S1Y = 30; // 第1个图的Y轴的起始点
	int E1Y = (height / 2) - height * 1 / 9; // 第一个图的Y轴的起终点
	int YB1GAP = (E1Y - S1Y - (height * 1 / 80)) / 6;
	int YS1GAP = YB1GAP / 5;

	int S2Y = (height / 2) - height * 1 / 40;// 第2个图的Y轴的起始点
	int E2Y = height - height * 2 / 9; // 第一个图的Y轴的起终点
	// 每一大行之间的距离
	int YB2GAP = (E2Y - S2Y - (height * 1 / 80)) / 5;
	// 每一小行之间的距离
	int YS2GAP = YB2GAP / 5;
	// 数据每单位之间的距离
	Double GAPY = ((double) (E2Y - S2Y - (double) (height * 1 / 80)) / 5 / 20);
	// 显示温度的起点
	int TX = width / 5;
	int TY = height * 9 / 20 + YS2GAP;

	// 湿度的起点
	int HX = width / 4;
	int HY = height * 151 / 180;

    private int mId;
    private String strOutDoor;
    
    public IndoorTempView(Context context, int id) {
		this(context, null);
        mId = id;
	}

	public IndoorTempView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init();
	}

	// 大行之间的间隔
	private Paint pt;
	private String strIndoor;

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
        
		/**
		 * 画温度的总体架子
		 */

		Log.e("宽度是:", "" + width);

		blackPAint.setStrokeWidth(4);
		// Y轴 最高处是30 总高470 每度的高度是GAPY
		if (isC) {
			canvas.drawLine(SX, E1Y, SX, S1Y, blackPAint);
		} else {
			canvas.drawLine(SX, E1Y + YS2GAP, SX, S1Y, blackPAint);
		}
		// X轴 最远处是EX 总长度是600
		canvas.drawLine(SX, E1Y, EX, E1Y, blackPAint);
		// Y轴 最高处是E2Y总高430 每度的高度是GAPY
		canvas.drawLine(SX, E2Y, SX, S2Y, blackPAint);
		// X轴 最远处是EX 总长度是600
		canvas.drawLine(SX, E2Y, EX, E2Y, blackPAint);
		blackPAint.setStrokeWidth(3);

		// 6大行 最上面一行不画出来
		for (int i = 0; i < 7; i++) {
			if (i != 6) {
				if (isC) {
					if (i == 0) {
						canvas.drawText("" + 20 * (i - 1), SX * 2 / 10, E1Y
								- (YB2GAP * i) + 10, blackPAint);
					} else {
						canvas.drawText("" + 20 * (i - 1), SX * 4 / 10, E1Y
								- (YB2GAP * i) + 10, blackPAint);
					}

				} else {
					if (i == 0) {
						canvas.drawLine(SX, E1Y + YS2GAP, EX, E1Y + YS2GAP,
								grayPAint);
						canvas.drawText("" + -4, SX * 4 / 10,
								E1Y + 10 + YS2GAP, blackPAint);
					} else if (i == 5) {
						canvas.drawText("" + 20 * (i), SX * 2 / 10, E1Y
								- (YB2GAP * i) + 10, blackPAint);
					} else {
						canvas.drawText("" + 20 * (i), SX * 4 / 10, E1Y
								- (YB2GAP * i) + 10, blackPAint);
					}

				}

				canvas.drawLine(SX, E1Y - (YB2GAP * i), EX, E1Y - (YB2GAP * i),
						blackPAint);
				if (i != 5) {
					canvas.drawText("" + 20 * i, SX * 4 / 10, E2Y
							- (YB2GAP * i) + 10, blackPAint);
					canvas.drawLine(SX, E2Y - (YB2GAP * i), EX, E2Y
							- (YB2GAP * i), blackPAint);
				} else {
					canvas.drawText("" + 20 * i, SX * 2 / 10, E2Y
							- (YB2GAP * i) + 10, blackPAint);
				}
			} else {
				if (isC) {
					canvas.drawText("" + 20 * (i - 1), SX * 2 / 10, E1Y
							- (YB2GAP * i) + 10, blackPAint);
				} else {
					canvas.drawText("" + 20 * (i), SX * 2 / 10, E1Y
							- (YB2GAP * i) + 10, blackPAint);
				}

			}

			// 划横线
			if (i > 0) {
				// 每大行之间四小行
				for (int m = 1; m < 5; m++) {
					canvas.drawLine(SX, E1Y - (YB2GAP * i) + (YS2GAP) * m, EX,
							E1Y - (YB2GAP * i) + (YS2GAP) * m, grayPAint);
				}
				if (i < 6) {
					for (int m = 1; m < 5; m++) {
						canvas.drawLine(SX, E2Y - (YB2GAP * i) + (YS2GAP) * m,
								EX, E2Y - (YB2GAP * i) + (YS2GAP) * m,
								grayPAint);
					}
				}
			}
		}

		/**
		 * 温度的具体数据
		 */
		int contants = 0;
		Path linePath = new Path();
		Path linePathH = new Path();
		for (int j = Constant.mlistIndoor.size() - 1; j >= 0; j--) {
			// X的变化坐标
			float Y = (float) (E1Y - (Constant.mlistIndoor.get(j).getTempC() + 20)
					* GAPY);
			float YF = (float) (E1Y - (Constant.mlistIndoor.get(j).getTempF()) * GAPY);
			float YH = (float) (E2Y - (Constant.mlistIndoor.get(j).getHumidity())
					* GAPY);
			int X = EX - 25 - (contants * GAP);

			if (isC) {
				if (Constant.mlistIndoor.get(j).getTempC() != -99) {
					// 绘制摄氏度的点
					canvas.drawCircle(X,// 起点X
							Y,// 起点Y
							2,// 半径
							paintData);
				}

				// 绘制线 各点之间的线
				if (j == (Constant.mlistIndoor.size() - 1)) {// 第一个点就是开始的地方
					// 开始的地方
					if (Constant.mlistIndoor.get(j).getTempC() != -99.0) {
						linePath.moveTo(X, Y);
					}
				} else {
					if (Constant.mlistIndoor.get(j).getTempC() != -99.0) {
						if (Constant.mlistIndoor.get(j + 1).getTempC() != -99.0) {
							linePath.lineTo(X, Y); //
						} else {
							linePath.moveTo(X, Y);
						}
					} else {
						
					}

				}
			} else {
				// 绘制华氏度的点
				if (Constant.mlistIndoor.get(j).getTempF() != -99.0) {
					canvas.drawCircle(X,// 起点X
							YF,// 起点Y
							2,// 半径
							paintData);
				}
				// 绘制线 各点之间的线
				if (j == (Constant.mlistIndoor.size() - 1)) {// 第一个点就是开始的地方
					// 开始的地方
					if (Constant.mlistIndoor.get(j).getTempF() != -99.0) {
						linePath.moveTo(X, YF);
					}
				} else {
					if (Constant.mlistIndoor.get(j).getTempF() != -99.0) {
						if (Constant.mlistIndoor.get(j + 1).getTempF() != -99.0) {
							linePath.lineTo(X, YF);
						} else {
							linePath.moveTo(X, YF);
						}
					} else {
					}
				}
			}
			canvas.drawText(Constant.mlistIndoor.get(j).getTime(), X - 25, E1Y
					+ height * 1 / 48 + YS2GAP, bluePAint);

			// 绘制湿度的点
			if (Constant.mlistIndoor.get(j).getHumidity() != -99) {
				canvas.drawCircle(X,// 起点X
						YH,// 起点Y
						2,// 半径
						paintF);
			}
			// 绘制线 各点之间的线
			if (j == (Constant.mlistIndoor.size() - 1)) {// 第一个点就是开始的地方
				// 开始的地方
				if (Constant.mlistIndoor.get(j).getHumidity() != -99) {
					linePathH.moveTo(X, YH);
				}
			} else {
				if (Constant.mlistIndoor.get(j).getHumidity() != -99) {
					if (Constant.mlistIndoor.get(j + 1).getTempF() != -99) {
						linePathH.lineTo(X, YH);
					} else {
						linePathH.moveTo(X, YH);
					}
				} else {

				}
			}
			canvas.drawText(Constant.mlistIndoor.get(j).getTime(), X - 25, E2Y
					+ height * 1 / 48, bluePAint);
			contants++;
			// 每次最多只画10条数据 超出的部分不画
			if (width < 500) {
				if (contants >= 7) {
					break;
				}
			} else {
				if (contants >= 10) {
					break;
				}
			}

		}
		canvas.drawPath(linePath, redPAint);
		canvas.drawPath(linePathH, blackPath);

		pt.setTextSize(40);
		if (Constant.mlistIndoor.size() > 0) {
			if (isC) {
                canvas.drawText(
						context.getString(R.string.indoor_temperature)
								+ Constant.mlistIndoor.get(Constant.mlistIndoor.size() - 1)
										.getStrC() + " ℃", TX, TY, pt);
			} else {
				canvas.drawText(
						context.getString(R.string.indoor_temperature)
								+ Constant.mlistIndoor.get(Constant.mlistIndoor.size() - 1)
										.getStrF() + " ℉", TX, TY, pt);
			}
			canvas.drawText(
					context.getString(R.string.indoor_humidity)
							+ Constant.mlistIndoor.get(Constant.mlistIndoor.size() - 1)
									.getStrH() + " %", HX, HY, pt);
		} else {
			canvas.drawText(context.getString(R.string.indoor_temperature)+"--", TX, TY, pt);
			canvas.drawText(context.getString(R.string.indoor_humidity)+"--", HX, HY, pt);
		}

	}

	public void init() {
		// 模拟数据
		grayPAint = new Paint();
		grayPAint.setColor(Color.GRAY);// 画笔颜色
		grayPAint.setStyle(Style.STROKE);// 描边
		grayPAint.setTextSize(13);// 字体大小

		blackPAint = new Paint();
		blackPAint.setColor(Color.BLACK);// �ʵ���ɫ
		blackPAint.setTextSize(25);// ���ֵĴ�С

		pt = new Paint();
		pt.setColor(Color.BLACK);// �ʵ���ɫ

		bluePAint = new Paint();
		bluePAint.setColor(Color.BLUE);// �ʵ���ɫ
		bluePAint.setStyle(Style.STROKE);
		bluePAint.setAntiAlias(true);
		bluePAint.setTextSize(20);// ���ֵĴ�С

		redPAint = new Paint();
		redPAint.setColor(Color.RED);// �ʵ���ɫ
		redPAint.setStyle(Style.STROKE);
		redPAint.setAntiAlias(true);
		redPAint.setTextSize(20);// ���ֵĴ�С

		blackPath = new Paint();
		blackPath.setColor(Color.BLUE);// �ʵ���ɫ
		blackPath.setStyle(Style.STROKE);
		blackPath.setAntiAlias(true);
		blackPath.setTextSize(20);// ���ֵĴ�С

		paintData = new Paint();
		paintData.setColor(Color.RED);
		paintData.setStrokeWidth(3);// 设置描边的宽度
		paintData.setAntiAlias(true);// 是否反锯齿
		paintData.setStyle(Paint.Style.STROKE);// 设置绘制的风格只有描边

		paintF = new Paint();
		paintF.setColor(Color.BLUE);
		paintF.setStrokeWidth(3);// 设置描边的宽度
		paintF.setAntiAlias(true);// 是否反锯齿
		paintF.setStyle(Paint.Style.STROKE);// 设置绘制的风格只有描边
		// 从本地SP文件中获取设备mac地址
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				"test", Activity.MODE_PRIVATE);
		String mac = sharedPreferences.getString("Adress", "");

		strIndoor = "**" + mac + "**temp**humidity**";//这是indoor的
        strOutDoor = "**" + mac + "**temp*#humidity**";


		// Constant.test();
		toStart();
		if (width < 500) {
			GAP = (EX - SX - 50) / 6;
			TX = width / 100;
			HX = width / 9;
		}

		// 获取温度是摄氏度还是华氏度
		SharedPreferences get_temperature = context.getSharedPreferences(
				"temperature", Activity.MODE_PRIVATE);
		// 从本地sp文件中获取温度的值 如果获得的值不是空 则将该控件的值设置为该值
		String temperature = get_temperature.getString("temperature", "°C");
		if ("°C".equals(temperature)) {
			isC = true;
		} else {
			isC = false;
		}
	}

	public void stopThread() {
		if (thread != null) {
			thread.interrupt();
		}
	}

	public void toStart() {
		// 开启一个线程 让其内部死循环
		thread = new Thread() {
			public void run() {
				while (true) {
					try {
						if (gt == null) {
							gt = new GetTempNo(handler);
                                gt.getTemp(strIndoor,ID_FROM_INDOOR);
							postInvalidate();
						}
						Thread.sleep(60 * 1000);
						int i = 0;
						Log.e("运行了：", "" + i++);
						gt.getTemp(strIndoor,ID_FROM_INDOOR);
						postInvalidate();
					} catch (InterruptedException e) {
						e.printStackTrace();
						return;
					}
				}
			}
		};
		thread.start();
	}

	Handler handler = new Handler() {
		private AlertDialog.Builder builder;

		public void handleMessage(Message msg) {
			if (msg.what == -1) {
				Log.e("发送来的what是-1", "发送来的what是-1");
				Toast.makeText(context, "网络异常", Toast.LENGTH_LONG);
				stopThread();
				if (builder == null) {
					builder = new AlertDialog.Builder(context);
					builder.setMessage("Network exception, please check the network!"); // 设置内容
					builder.setPositiveButton("OK",
							new DialogInterface.OnClickListener() { // 设置确定按钮
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									toStart();
									dialog.dismiss();
								}
							});
				}
				builder.show();
			} else {
				Log.e("发送来的what是1", "发送来的what是1");
			}
		};
	};

}
