//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// UsePaletteProviderFragment.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments;

import com.scichart.charting.model.dataSeries.IOhlcDataSeries;
import com.scichart.charting.model.dataSeries.IXyDataSeries;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.annotations.AnnotationCoordinateMode;
import com.scichart.charting.visuals.annotations.BoxAnnotation;
import com.scichart.charting.visuals.annotations.IAnnotation;
import com.scichart.charting.visuals.annotations.OnAnnotationDragListener;
import com.scichart.charting.visuals.axes.NumericAxis;
import com.scichart.charting.visuals.renderableSeries.paletteProviders.IFillPaletteProvider;
import com.scichart.charting.visuals.renderableSeries.paletteProviders.IPointMarkerPaletteProvider;
import com.scichart.charting.visuals.renderableSeries.paletteProviders.IStrokePaletteProvider;
import com.scichart.charting.visuals.renderableSeries.paletteProviders.PaletteProviderBase;
import com.scichart.charting.visuals.pointmarkers.EllipsePointMarker;
import com.scichart.charting.visuals.pointmarkers.SquarePointMarker;
import com.scichart.charting.visuals.renderableSeries.IRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.OhlcRenderableSeriesBase;
import com.scichart.charting.visuals.renderableSeries.XyRenderableSeriesBase;
import com.scichart.charting.visuals.renderableSeries.data.OhlcRenderPassData;
import com.scichart.charting.visuals.renderableSeries.data.XyRenderPassData;
import com.scichart.core.model.DoubleValues;
import com.scichart.core.model.IntegerValues;
import com.scichart.drawing.utility.ColorUtil;
import com.scichart.examples.R;
import com.scichart.examples.data.DataManager;
import com.scichart.examples.data.PriceSeries;
import com.scichart.examples.fragments.base.ExampleBaseFragment;

import java.util.Collections;

import butterknife.Bind;

public class UsePaletteProviderFragment extends ExampleBaseFragment {
    @Bind(R.id.chart)
    SciChartSurface chart;

    @Override
    protected int getLayoutId() {
        return R.layout.example_single_chart_fragment;
    }

    @Override
    protected void initExample() {
        final NumericAxis xAxis = sciChartBuilder.newNumericAxis()
                .withAxisTitle("Time")
                .build();

        final NumericAxis yAxis = sciChartBuilder.newNumericAxis()
                .withAxisTitle("Value")
                .withTextFormatting("#.0")
                .withGrowBy(0.1, 0.1)
                .build();

        final DataManager dataManager = DataManager.getInstance();
        final PriceSeries priceBars = dataManager.getPriceDataIndu(getActivity());
        final double dataOffset = -1000;

        final IXyDataSeries<Double, Double> mountainSeries = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Mountain Series").build();
        final IXyDataSeries<Double, Double> lineSeries = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Line Series").build();
        final IXyDataSeries<Double, Double> columnSeries = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Column Series").build();
        final IXyDataSeries<Double, Double> xyScatterSeries = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Scatter Series").build();
        final IOhlcDataSeries<Double, Double> candlestickSeries = sciChartBuilder.newOhlcDataSeries(Double.class, Double.class).withSeriesName("Candlestick Series").build();
        final IOhlcDataSeries<Double, Double> ohlcSeries = sciChartBuilder.newOhlcDataSeries(Double.class, Double.class).withSeriesName("OHLC Series").build();

        mountainSeries.append(priceBars.getIndexesAsDouble(), dataManager.offset(priceBars.getLowData(), dataOffset * 2));
        lineSeries.append(priceBars.getIndexesAsDouble(), dataManager.offset(priceBars.getCloseData(), -dataOffset));
        ohlcSeries.append(priceBars.getIndexesAsDouble(), priceBars.getOpenData(), priceBars.getHighData(), priceBars.getLowData(), priceBars.getCloseData());
        candlestickSeries.append(priceBars.getIndexesAsDouble(),
                dataManager.offset(priceBars.getOpenData(), dataOffset),
                dataManager.offset(priceBars.getHighData(), dataOffset),
                dataManager.offset(priceBars.getLowData(), dataOffset),
                dataManager.offset(priceBars.getCloseData(), dataOffset));
        columnSeries.append(priceBars.getIndexesAsDouble(), dataManager.offset(priceBars.getCloseData(), dataOffset * 3));
        xyScatterSeries.append(priceBars.getIndexesAsDouble(), dataManager.offset(priceBars.getOpenData(), dataOffset * 2.5));

        final BoxAnnotation annotation = sciChartBuilder.newBoxAnnotation().withPosition(50d, 0, 100d, 1).withBackgroundDrawableId(R.drawable.example_box_annotation_background_1).withIsEditable(true).withCoordinateMode(AnnotationCoordinateMode.RelativeY).build();
        annotation.setOnAnnotationDragListener(new OnAnnotationDragListener() {
            @Override
            public void onDragStarted(IAnnotation annotation) {
                updateAnnotation(annotation);
            }

            protected void updateAnnotation(IAnnotation annotation) {
                annotation.setY1(0);
                annotation.setY2(1);
                chart.invalidateElement();
            }

            @Override
            public void onDragEnded(IAnnotation annotation) {
                updateAnnotation(annotation);
            }

            @Override
            public void onDragDelta(IAnnotation annotation, float horizontalOffset, float verticalOffset) {
                updateAnnotation(annotation);
            }
        });

        final IRenderableSeries mountainRenderableSeries = sciChartBuilder.newMountainSeries()
                .withAreaFillColor(0x9787CEEB)
                .withStrokeStyle(ColorUtil.Magenta)
                .withDataSeries(mountainSeries)
                .withPaletteProvider(new XyCustomPaletteProvider(ColorUtil.Red, annotation))
                .build();

        final IRenderableSeries lineRenderableSeries = sciChartBuilder.newLineSeries()
                .withStrokeStyle(ColorUtil.Blue)
                .withPointMarker(sciChartBuilder.newPointMarker(new EllipsePointMarker()).withFill(ColorUtil.Red).withStroke(ColorUtil.Orange, 2f).withSize(10, 10).build())
                .withDataSeries(lineSeries)
                .withPaletteProvider(new XyCustomPaletteProvider(ColorUtil.Red, annotation))
                .build();

        final IRenderableSeries ohlcRenderableSeries = sciChartBuilder.newOhlcSeries()
                .withDataSeries(ohlcSeries)
                .withPaletteProvider(new OhlcCustomPaletteProvider(ColorUtil.CornflowerBlue, annotation))
                .build();

        final IRenderableSeries candlestickRenderableSeries = sciChartBuilder.newCandlestickSeries()
                .withDataSeries(candlestickSeries)
                .withPaletteProvider(new OhlcCustomPaletteProvider(ColorUtil.Green, annotation))
                .build();

        final IRenderableSeries columnRenderableSeries = sciChartBuilder.newColumnSeries()
                .withStrokeStyle(ColorUtil.Blue)
                .withZeroLine(6000)
                .withDataPointWidth(0.8d)
                .withFillColor(ColorUtil.Blue)
                .withDataSeries(columnSeries)
                .withPaletteProvider(new XyCustomPaletteProvider(ColorUtil.Purple, annotation))
                .build();

        final IRenderableSeries xyScatterRenderableSeries = sciChartBuilder.newScatterSeries()
                .withDataSeries(xyScatterSeries)
                .withPointMarker(sciChartBuilder.newPointMarker(new SquarePointMarker()).withFill(ColorUtil.Red).withStroke(ColorUtil.Orange, 2f).withSize(7, 7).build())
                .withPaletteProvider(new XyCustomPaletteProvider(ColorUtil.LimeGreen, annotation))
                .build();

        Collections.addAll(chart.getChartModifiers(), sciChartBuilder.newModifierGroupWithDefaultModifiers().build());
        Collections.addAll(chart.getXAxes(), xAxis);
        Collections.addAll(chart.getYAxes(), yAxis);
        Collections.addAll(chart.getRenderableSeries(), mountainRenderableSeries, lineRenderableSeries, ohlcRenderableSeries, candlestickRenderableSeries, columnRenderableSeries, xyScatterRenderableSeries);
        Collections.addAll(chart.getAnnotations(), annotation);
    }

    private static final class XyCustomPaletteProvider extends PaletteProviderBase<XyRenderableSeriesBase> implements IFillPaletteProvider, IStrokePaletteProvider, IPointMarkerPaletteProvider {
        private final IntegerValues colors = new IntegerValues();

        private final int color;
        private final BoxAnnotation annotation;

        public XyCustomPaletteProvider(int color, BoxAnnotation annotation) {
            super(XyRenderableSeriesBase.class);
            this.color = color;
            this.annotation = annotation;
        }

        @Override
        public void update() {
            final XyRenderableSeriesBase renderableSeries = this.renderableSeries;
            final XyRenderPassData currentRenderPassData = (XyRenderPassData) renderableSeries.getCurrentRenderPassData();
            final DoubleValues xValues = currentRenderPassData.xValues;

            final int size = currentRenderPassData.pointsCount();
            colors.setSize(size);

            final double x1 = (Double)annotation.getX1();
            final double x2 = (Double)annotation.getX2();

            final double min = Math.min(x1, x2);
            final double max = Math.max(x1, x2);

            final int[] colorsArray = colors.getItemsArray();
            final double[] valuesArray = xValues.getItemsArray();

            for (int i = 0; i < size; i++) {
                final double value = valuesArray[i];
                if (value > min && value < max)
                    colorsArray[i] = color;
                else
                    colorsArray[i] = PaletteProviderBase.DEFAULT_COLOR;
            }
        }

        @Override
        public IntegerValues getFillColors() {
            return colors;
        }

        @Override
        public IntegerValues getPointMarkerColors() {
            return colors;
        }

        @Override
        public IntegerValues getStrokeColors() {
            return colors;
        }
    }

    private static final class OhlcCustomPaletteProvider extends PaletteProviderBase<OhlcRenderableSeriesBase> implements IFillPaletteProvider, IStrokePaletteProvider, IPointMarkerPaletteProvider {
        private final IntegerValues colors = new IntegerValues();

        private final int color;
        private final BoxAnnotation annotation;

        public OhlcCustomPaletteProvider(int color, BoxAnnotation annotation) {
            super(OhlcRenderableSeriesBase.class);
            this.color = color;
            this.annotation = annotation;
        }

        @Override
        public void update() {
            final OhlcRenderableSeriesBase renderableSeries = this.renderableSeries;
            final OhlcRenderPassData currentRenderPassData = (OhlcRenderPassData) renderableSeries.getCurrentRenderPassData();
            final DoubleValues xValues = currentRenderPassData.xValues;

            final int size = currentRenderPassData.pointsCount();
            colors.setSize(size);

            final double x1 = (Double)annotation.getX1();
            final double x2 = (Double)annotation.getX2();

            final double min = Math.min(x1, x2);
            final double max = Math.max(x1, x2);

            final int[] colorsArray = colors.getItemsArray();
            final double[] valuesArray = xValues.getItemsArray();

            for (int i = 0; i < size; i++) {
                final double value = valuesArray[i];
                if (value > min && value < max)
                    colorsArray[i] = color;
                else
                    colorsArray[i] = PaletteProviderBase.DEFAULT_COLOR;
            }
        }

        @Override
        public IntegerValues getFillColors() {
            return colors;
        }

        @Override
        public IntegerValues getPointMarkerColors() {
            return colors;
        }

        @Override
        public IntegerValues getStrokeColors() {
            return colors;
        }
    }

    public boolean showDefaultModifiersInToolbar() {
        return false;
    }
}