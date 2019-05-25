package thito.resourcebanner;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

//
//GifSequenceWriter.java
//
//Created by Elliot Kroo on 2009-04-25.
//
//This work is licensed under the Creative Commons Attribution 3.0 Unported
//License. To view a copy of this license, visit
//http://creativecommons.org/licenses/by/3.0/ or send a letter to Creative
//Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
import javax.imageio.IIOException;

public class GifSequenceWriter {
	AnimatedGifEncoder encoder = new AnimatedGifEncoder();
	OutputStream out;
	public GifSequenceWriter(OutputStream output, int imageType, int timeBetweenFramesMS, boolean loopContinuously)
	throws IIOException, IOException {
		encoder.setDelay(timeBetweenFramesMS/10);
		encoder.setDispose(0);
		encoder.setRepeat(0);
		encoder.setQuality(1);
		out = output;

	}
	public void writeToSequence(BufferedImage img) throws IOException {
		if (!encoder.isStarted()) encoder.start(out);
		encoder.addFrame(img);
	}
	public void close() {
		encoder.finish();
	}
	//old
//	/**
//	 * Returns an existing child node, or creates and returns a new child node
//	 * (if the requested node does not exist).
//	 * 
//	 * @param rootNode
//	 *            the <tt>IIOMetadataNode</tt> to search for the child node.
//	 * @param nodeName
//	 *            the name of the child node.
//	 * 
//	 * @return the child node, if found or a new node created with the given
//	 *         name.
//	 */
//	private static IIOMetadataNode getNode(IIOMetadataNode rootNode, String nodeName) {
//		final int nNodes = rootNode.getLength();
//		for (int i = 0; i < nNodes; i++) {
//			if (rootNode.item(i).getNodeName().compareToIgnoreCase(nodeName) == 0) {
//				return (IIOMetadataNode) rootNode.item(i);
//			}
//		}
//		final IIOMetadataNode node = new IIOMetadataNode(nodeName);
//		rootNode.appendChild(node);
//		return node;
//	}
//
//	/**
//	 * Returns the first available GIF ImageWriter using
//	 * ImageIO.getImageWritersBySuffix("gif").
//	 * 
//	 * @return a GIF ImageWriter object
//	 * @throws IIOException
//	 *             if no GIF image writers are returned
//	 */
//	private static ImageWriter getWriter() throws IIOException {
//		final Iterator<ImageWriter> iter = ImageIO.getImageWritersBySuffix("gif");
//		if (!iter.hasNext()) {
//			throw new IIOException("No GIF Image Writers Exist");
//		} else {
//			return iter.next();
//		}
//	}
//
//	public static void main(String[] args) throws Throwable {
//		System.out.println(0xFF);
//	}
//
//	protected ByteArrayImageOutputStream cache;
//
//	/**
//	 * public GifSequenceWriter( BufferedOutputStream outputStream, int
//	 * imageType, int timeBetweenFramesMS, boolean loopContinuously) {
//	 * 
//	 */
//
//	protected ImageWriter gifWriter;
//
//	protected IIOMetadata imageMetaData;
//
//	protected ImageWriteParam imageWriteParam;
//
//	/**
//	 * Creates a new GifSequenceWriter
//	 * 
//	 * @param outputStream
//	 *            the ImageOutputStream to be written to
//	 * @param imageType
//	 *            one of the imageTypes specified in BufferedImage
//	 * @param timeBetweenFramesMS
//	 *            the time between frames in miliseconds
//	 * @param loopContinuously
//	 *            wether the gif should loop repeatedly
//	 * @throws IIOException
//	 *             if no gif ImageWriters are found
//	 *
//	 * @author Elliot Kroo (elliot[at]kroo[dot]net)
//	 */
//	public GifSequenceWriter(OutputStream output, int imageType, int timeBetweenFramesMS, boolean loopContinuously)
//			throws IIOException, IOException {
//		cache = new ByteArrayImageOutputStream(output);
//		// my method to create a writer
//		gifWriter = getWriter();
//		imageWriteParam = gifWriter.getDefaultWriteParam();
//		final ImageTypeSpecifier imageTypeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(imageType);
//
//		imageMetaData = gifWriter.getDefaultImageMetadata(imageTypeSpecifier, imageWriteParam);
//
//		final String metaFormatName = imageMetaData.getNativeMetadataFormatName();
//
//		final IIOMetadataNode root = (IIOMetadataNode) imageMetaData.getAsTree(metaFormatName);
//
//		final IIOMetadataNode graphicsControlExtensionNode = getNode(root, "GraphicControlExtension");
//		graphicsControlExtensionNode.setAttribute("disposalMethod", "none");
//		graphicsControlExtensionNode.setAttribute("userInputFlag", "FALSE");
//		graphicsControlExtensionNode.setAttribute("transparentColorFlag", "TRUE");
//		graphicsControlExtensionNode.setAttribute("delayTime", Integer.toString(timeBetweenFramesMS / 10));
//		graphicsControlExtensionNode.setAttribute("transparentColorIndex", "0");
//
//		final IIOMetadataNode commentsNode = getNode(root, "CommentExtensions");
//		commentsNode.setAttribute("CommentExtension", "ResourceBanner GIF");
//
//		final IIOMetadataNode appEntensionsNode = getNode(root, "ApplicationExtensions");
//
//		final IIOMetadataNode child = new IIOMetadataNode("ApplicationExtension");
//
//		child.setAttribute("applicationID", "NETSCAPE");
//		child.setAttribute("authenticationCode", "2.0");
//
//		final int loop = loopContinuously ? 0 : 1;
//
//		child.setUserObject(new byte[] { 0x1, (byte) (loop & 0xFF), (byte) (loop >> 8 & 0xFF) });
//		appEntensionsNode.appendChild(child);
//
//		imageMetaData.setFromTree(metaFormatName, root);
//
//		gifWriter.setOutput(cache);
//		gifWriter.prepareWriteSequence(null);
//	}
//
//	/**
//	 * Close this GifSequenceWriter object. This does not close the underlying
//	 * stream, just finishes off the GIF.
//	 */
//	public void close() throws IOException {
//		gifWriter.endWriteSequence();
//		cache.close();
//	}
//
//	public void writeToSequence(RenderedImage img) throws IOException {
//		gifWriter.writeToSequence(new IIOImage(img, null, imageMetaData), imageWriteParam);
//	}
}