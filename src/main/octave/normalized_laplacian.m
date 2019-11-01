function L = normalized_laplacian(A)
    n = size(A)(1);
    D = diag(sum(A, 2));
    L = eye(n, n) - D^(-0.5) * A * D^(-0.5);
endfunction