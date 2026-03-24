const Pagination = ({data, onPageChange}) => {

    console.log('data',data)

    const { totalPages, number: currentPage, first, last } = data;
    const pageNumbers = Array.from({ length: totalPages }, (_, index) => index);

    
    return (
        <div className="flex items-center justify-center gap-2 mt-4">
            {/* 이전 버튼 */}
            <button
                disabled={first}
                onClick={() => onPageChange(currentPage - 1)}
                className="px-3 py-1 border rounded disabled:opacity-50"
            >
                이전
            </button>

            {/* 페이지 번호들 */}
            {pageNumbers.map((pageIdx) => (
                <button
                key={pageIdx}
                onClick={() => onPageChange(pageIdx)}
                className={`px-3 py-1 border rounded ${
                    currentPage === pageIdx ? 'bg-blue-500 text-white' : 'bg-white'
                }`}
                >
                {pageIdx + 1}
                </button>
            ))}

            {/* 다음 버튼 */}
            <button
                disabled={last}
                onClick={() => onPageChange(currentPage + 1)}
                className="px-3 py-1 border rounded disabled:opacity-50"
            >
                다음
            </button>
        </div>
    )
}

export default Pagination;