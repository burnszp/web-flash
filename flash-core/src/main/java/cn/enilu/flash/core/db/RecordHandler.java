package cn.enilu.flash.core.db;

public interface RecordHandler<T> {

	void process(T record);

}
